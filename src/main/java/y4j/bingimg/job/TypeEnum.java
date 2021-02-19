package y4j.bingimg.job;

public enum TypeEnum {
	HD("HD", "1920x1200"), UHD("UHD", "UHD"), PHONE("PHONE", "1080x1920");
	
	private final String type;
	private final String size;
	
	public String getSize() {
		return size;
	}
	
	public String getType() {
		return type;
	}
	
	TypeEnum(String type, String size) {
		this.type = type;
		this.size = size;
	}
}
