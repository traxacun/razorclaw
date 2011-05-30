package razorclaw.linguistic.filter;

public interface IStopwordFilter extends IFilter
{
	boolean isStopWord(String word);
	boolean isArticle(String word);
	void addStopWord(String word);
}
