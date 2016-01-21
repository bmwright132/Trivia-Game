/*
 * Message Types
 * type 0 = waiting for opponent
 * type 1 = start message
 * type 2 = update score
 * type 3 = opponent finished
 * type 4 = I'm finished
 * type 5 = closing message
 */
public class Message {
	private String name;
	private int type;
	private String content;
	private final String FIELDDEL="#";
	private String DELIMITER = "\r\n";
	public Message(String name, int type, String content) 
	{
		this.name = name;
		this.type = type;
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getmType() {
		return type;
	}
	public void setmType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return name + 
				FIELDDEL+type+
				FIELDDEL+content+
				DELIMITER;
	}
	public void setMsg(String str)
	{
		String[] s1= str.split(FIELDDEL);
		name=s1[0];
		type=Integer.parseInt(s1[1]);
		content=s1[2];

	}
}
