package razorclaw.evaluation.google;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import com.google.api.adwords.lib.AdWordsService;
import com.google.api.adwords.lib.AdWordsServiceLogger;
import com.google.api.adwords.lib.AdWordsUser;
import com.google.api.adwords.lib.utils.MapUtils;
import com.google.api.adwords.v201101.cm.ApiException;
import com.google.api.adwords.v201101.cm.Keyword;
import com.google.api.adwords.v201101.cm.Paging;
import com.google.api.adwords.v201101.o.Attribute;
import com.google.api.adwords.v201101.o.AttributeType;
import com.google.api.adwords.v201101.o.CriterionAttribute;
import com.google.api.adwords.v201101.o.IdeaType;
import com.google.api.adwords.v201101.o.LongAttribute;
import com.google.api.adwords.v201101.o.RelatedToUrlSearchParameter;
import com.google.api.adwords.v201101.o.RequestType;
import com.google.api.adwords.v201101.o.SearchParameter;
import com.google.api.adwords.v201101.o.TargetingIdea;
import com.google.api.adwords.v201101.o.TargetingIdeaPage;
import com.google.api.adwords.v201101.o.TargetingIdeaSelector;
import com.google.api.adwords.v201101.o.TargetingIdeaServiceInterface;

public class AdWordsWrapper {

	private static AdWordsUser _user;

	private static final Logger LOG = Logger.getLogger(AdWordsWrapper.class
			.getName());

	private static final String USER_EMAIL = "adwdottk@gmail.com";

	private static final String USER_PASS = "kytr2223";

	private static final String ADS_ACCOUNT = "adwdottk@gmail.com";

	private static final String AFFLIATION = "University College London";

	private static final String DEV_TOKEN = "hCFHvQl2dABdaciUnWARBw";

	private static void init() {
		try {
			LOG.info("Started initialising AdwordsService.");

			// Log SOAP XML request and response.
			// AdWordsServiceLogger.log();

			_user = new AdWordsUser(USER_EMAIL, USER_PASS, ADS_ACCOUNT,
					AFFLIATION, DEV_TOKEN);

			LOG.info("Finished initialising AdwordsService.");
		} catch (Exception e) {
			LOG.severe("Failed to initialise AdwordsService.");
		}
	}

	public static String analyze(String url) throws ServiceException,
			ApiException, RemoteException {
		if (_user == null) {
			init();
		}

		// Get the TargetingIdeaService.
		TargetingIdeaServiceInterface targetingIdeaService = _user
				.getService(AdWordsService.V201101.TARGETING_IDEA_SERVICE);

		// Create selector.
		TargetingIdeaSelector selector = new TargetingIdeaSelector();
		selector.setRequestType(RequestType.IDEAS);
		selector.setIdeaType(IdeaType.KEYWORD);
		selector.setRequestedAttributeTypes(new AttributeType[] { AttributeType.CRITERION });

		// load forwardURL
		RelatedToUrlSearchParameter param = new RelatedToUrlSearchParameter();
		param.setUrls(new String[] { url });

		selector.setSearchParameters(new SearchParameter[] { param });

		// Set selector paging (required for targeting idea service).
		Paging paging = new Paging();
		paging.setStartIndex(0);
		paging.setNumberResults(3);
		selector.setPaging(paging);

		// Get related keywords.
		TargetingIdeaPage page = targetingIdeaService.get(selector);

		String ret = "";
		// Display related keywords.
		if (page.getEntries() != null && page.getEntries().length > 0) {
			Keyword keyword = new Keyword();

			for (TargetingIdea targetingIdea : page.getEntries()) {
				Map<AttributeType, Attribute> data = MapUtils
						.toMap(targetingIdea.getData());
				keyword = (Keyword) ((CriterionAttribute) data
						.get(AttributeType.CRITERION)).getValue();

				ret += keyword.getText() + ", ";
			}
		}

		return ret.substring(0, ret.length() - 2);
	}
}