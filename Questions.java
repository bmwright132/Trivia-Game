/*
 * Brandon Wright, Jake Snow, Jared Aarons
 * Dr.Liu
 * December 3rd, 2015
 * This class deals with the questions and answers for each client
 */
public class Questions 
{
	private String question;
	private String [] choices = new String[4];
	private String correct;
	
	public Questions(String q, String[] ans, String right)
	{
		question=q;
		choices = ans;
		correct = right;
	}
	public String getAnswer()
	{
		return correct;
	}
	public String getChoice(int i)
	{
		if(i>3)
			throw new IndexOutOfBoundsException();
		else
			return choices[i];
	}
	public String getQuestion()
	{
		return question;
	}
	public String toString()
	{
		return question+" "+correct;
	}

}
