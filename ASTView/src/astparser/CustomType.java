package astparser;

public class CustomType implements Comparable <CustomType> {
	
	private int number;
	private String value;
	private String name;

	@Override
	public int compareTo(CustomType o) {
		if (number == o.number) {
			return o.value.compareTo(value);
		}
		return o.number - number;
	}
	
	public CustomType (String s , int i) {
		this.number = i;
		this.value = s;
		this.name = s;
	}
	
	public CustomType(String s, int i, String name) {
		this.number = i;
		this.value = s;
		this.name = name;
	}
	
	public void setName(String s){
		this.name = s;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.name + " : " + this.number;
	}
	
	
	

}
