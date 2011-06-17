package razorclaw.evaluation.razorclaw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.EigenDecompositionImpl;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

import razorclaw.object.APIMeta;
import razorclaw.object.KeyPhraseScore;
import razorclaw.object.PhraseProperty;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;

public class UCBRanker {
	private static final double PARA_Delta = 1;

	private static final double PARA_B = 0.4;

	// average length(number of phrases) for different features
	private static final double _avgTitleLength = 3.5,
			_avgMetaKeywordsLength = 5.5, _avgMetaDescriptionLength = 10.5,
			_avgH1Length = 5.5, _avgH2Length = 10.5, _avgContentLength = 123.5,
			_avgAnchorLength = 4.5;

	public static ArrayList<KeyPhraseScore> rank(
			Webpage web,
			RealMatrix mHistoricalFeatureX,
			int iterationCount,
			double[][] historicalReward) {
		ArrayList<KeyPhraseScore> ret = new ArrayList<KeyPhraseScore>();

		WebpageMeta webpageMeta = web.getWebpageMeta();
		APIMeta apiMeta = web.getAPIMeta();

		// rank each phrase
		for (Entry<String, PhraseProperty> e : web.getPhraseMap().entrySet()) {
			KeyPhraseScore kps = new KeyPhraseScore();
			kps.setPhrase(e.getKey());

			// get score on every field

			// --------------title-------------------
			double titleScore = e.getValue().getTFTitle()
					/ ((1 - PARA_B) + PARA_B * webpageMeta.getTitle().size()
							/ _avgTitleLength);
			kps.setTitleScore(titleScore);
			// ---------------meta keywords---------------
			double metaKeywordsScore = e.getValue().getTFMetaKeywords()
					/ ((1 - PARA_B) + PARA_B * webpageMeta.getKeywords().size()
							/ _avgMetaKeywordsLength);
			kps.setMetaKeywordsScore(metaKeywordsScore);
			// --------------meta description-----------------
			double metaDescriptionScore = e.getValue().getTFMetaDescription()
					/ ((1 - PARA_B) + PARA_B
							* webpageMeta.getDescription().size()
							/ _avgMetaDescriptionLength);
			kps.setMetaDescriptionScore(metaDescriptionScore);
			// ----------------------h1---------------------
			double h1Score = e.getValue().getTFH1()
					/ ((1 - PARA_B) + PARA_B * webpageMeta.getH1().size()
							/ _avgH1Length);
			kps.setH1Score(h1Score);
			// ----------------------h2------------------
			double h2Score = e.getValue().getTFH2()
					/ ((1 - PARA_B) + PARA_B * webpageMeta.getH2().size()
							/ _avgH2Length);
			kps.setH2Score(h2Score);
			// --------------------content-------------------
			double contentScore = e.getValue().getTFContent()
					/ ((1 - PARA_B) + PARA_B * web.getPhraseMap().size()
							/ _avgContentLength);
			kps.setContentScore(contentScore);
			// ------------anchor text----------------
			double anchorTextScore = e.getValue().getTFAnchor()
					/ ((1 - PARA_B) + PARA_B
							* apiMeta.getRefererAnchorTexts().size()
							/ _avgAnchorLength);
			kps.setAnchorTextScore(anchorTextScore);
			// -----------spider keywords-------------
			// double spiderKeywordsScore = 0.0;
			// if (e.getValue().isSpiderKeywords()) {
			// spiderKeywordsScore = 1;
			// }
			// kps.setSpiderKeywordsScore(spiderKeywordsScore);
			// -----------admin keywords-------------
			double adminKeywordsScore = 0.0;
			if (e.getValue().isAdminKeywords()) {
				adminKeywordsScore = 1;
			}
			kps.setAdminKeywordsScore(adminKeywordsScore);
			// -----------user keywords-------------
			double userKeywordsScore = 0.0;
			if (e.getValue().isUserKeywords()) {
				userKeywordsScore = 1;
			}
			kps.setUserKeywordsScore(userKeywordsScore);
			// -----------search query-------------
			// double searchQueryScore = e.getValue().getTFSearchQuery()
			// / ((1 - _paraB) + _paraB
			// * e.getKey().length() / _avgSearchQueryLength);
			// kps.setSearchQueryScore(searchQueryScore);
			// -------------part of speech-----------
			double POSScore = 0.0;
			if (e.getValue().getPartOfSpeech().equals("NOUN")) {
				POSScore = 1.0;
			}
			kps.setPOSScore(POSScore);

			// construct x_i, the feature vector for the phrase
			// 1*d
			// @formatter:off
			double[][] x = { 
				{titleScore, 
				metaKeywordsScore, 
				metaDescriptionScore,
				h1Score, 
				h2Score, 
				contentScore, 
				anchorTextScore,
				adminKeywordsScore, 
				userKeywordsScore,
				POSScore}
			};
			// @formatter:on
			int dimension = x[0].length;
			RealMatrix mLocalFeatureX = new Array2DRowRealMatrix(x);

			// solve for a_i, the weight matrix which converts x_i to a linear
			// combination of X

			// solve for eigenvalue decomposition of X(t)X(t)'
			RealMatrix inner = mHistoricalFeatureX.transpose().multiply(
					mHistoricalFeatureX);

			EigenDecompositionImpl innerDecomposition = new EigenDecompositionImpl(
					inner, 0.0);
			RealMatrix matrixU = innerDecomposition.getV();
			RealMatrix matrixDelta = innerDecomposition.getD();

			// d*1
			RealMatrix mZ = matrixU.multiply(mLocalFeatureX.transpose());
			assert mZ.getRowDimension() == dimension;
			assert mZ.getColumnDimension() == 1;

			int posK = 0;
			for (int i = 0; i < matrixDelta.getRowDimension(); i++) {
				if (matrixDelta.getRow(i)[i] >= 1) {
					posK = i;
				} else {
					break;
				}
			}

			// d*1
			RealMatrix mU = new Array2DRowRealMatrix(new double[dimension][1]);
			RealMatrix mV = new Array2DRowRealMatrix(new double[dimension][1]);

			for (int i = 0; i < posK; i++) {
				mU.getRow(i)[0] = mZ.getRow(i)[0];
			}
			for (int i = posK; i < dimension; i++) {
				mV.getRow(i)[0] = mZ.getRow(i)[0];
			}

			// d*d
			RealMatrix mNewDelta = new Array2DRowRealMatrix(
					new double[dimension][dimension]);
			for (int i = 0; i < posK; i++) {
				mNewDelta.getRow(i)[i] = 1 / matrixDelta.getRow(i)[i];
			}

			// 1*t
			RealMatrix mA = mU.transpose().multiply(mNewDelta)
					.multiply(matrixU)
					.multiply(mHistoricalFeatureX.transpose());
			assert mA.getRowDimension() == 1;
			assert mA.getColumnDimension() == iterationCount;

			// solve for width and upper confidence bound
			double normA = mA.getNorm();
			double width = normA
					* Math.sqrt(Math.log(2 * iterationCount
							* web.getPhraseMap().size() / PARA_Delta))
					+ mV.getNorm();

			// solve for upper confidence bound
			RealMatrix mHistoryReward = new Array2DRowRealMatrix(
					historicalReward);

			double expectedReward = mHistoryReward.multiply(mA.transpose())
					.getEntry(0, 0);

			double UCBScore = expectedReward + width;

			kps.setUCBScore(UCBScore);
		}

		return ret;
	}
}
