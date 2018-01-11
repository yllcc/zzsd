package cn.com.fotic.eimp.repository.entity;
/**
 * 性别分数计算
 * @author yangll
 */
public enum SexEnum {
	BOY("男性",42),GIRL("女性",79),OTHER("女性",42);
	private String sex;
	
	private int score;
	 // 构造方法  
    private SexEnum(String sex, int score) {  
        this.sex = sex;  
        this.score = score;  
    }  
    // 普通方法  
    public static int getSexScore(String sex) {  
        for (SexEnum c : SexEnum.values()) {  
            if (c.getSex().equals(sex)) {  
                return c.score;  
            } 
        }  
        return SexEnum.OTHER.getScore();  
    }
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}  
	
}
