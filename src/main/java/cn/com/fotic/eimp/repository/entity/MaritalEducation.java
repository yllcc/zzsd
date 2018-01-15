package cn.com.fotic.eimp.repository.entity;
/**
 * 婚姻学历权重分
 * @author yangll
 */
public class MaritalEducation { 
	/**
	 * 根据婚姻状态判别不同学历分数
	 * @param bankCredit
	 * @return int
	 */
	public static int getMaritalEducationScore(BankCredit bankCredit){
		if(MaritalStatus.UNMARRIED.getMaritalStatus().equals(bankCredit.getRateMaritalState())) {
			return UnmarriedEducationStatus.getstatusScore(bankCredit.getRateEduLevel());
		}else if(MaritalStatus.MARRIED.getMaritalStatus().equals(bankCredit.getRateMaritalState())) {
			return MarriedEducationStatus.getstatusScore(bankCredit.getRateEduLevel());
		}else if(MaritalStatus.DIVORCE.getMaritalStatus().equals(bankCredit.getRateMaritalState())) {
			return DivorceEducationStatus.getstatusScore(bankCredit.getRateEduLevel());
		}else if(MaritalStatus.WIDOWED.getMaritalStatus().equals(bankCredit.getRateMaritalState())) {
			return WidowedEducationStatus.getstatusScore(bankCredit.getRateEduLevel());
		}else if(MaritalStatus.OTHER.getMaritalStatus().equals(bankCredit.getRateMaritalState())) {
			return OtherEducationStatus.getstatusScore(bankCredit.getRateEduLevel());
		}
		return OtherEducationStatus.getstatusScore(bankCredit.getRateEduLevel());
	}
	
	//婚姻状态
	enum MaritalStatus{
		UNMARRIED("未婚"),MARRIED("已婚"),DIVORCE("离婚"),WIDOWED("丧偶"),OTHER("未知");
	    private String maritalStatus ;
		private MaritalStatus(String maritalStatus) {
			this.maritalStatus=maritalStatus;
		}
		public String getMaritalStatus() {
			return maritalStatus;
		}
		public void setMaritalStatus(String maritalStatus) {
			this.maritalStatus = maritalStatus;
		}
		
	}  
	//未婚对应的学历分值
     enum  UnmarriedEducationStatus{  
    	POSTGRADUATE("硕士",105),UNDERGRADUATE("本科",105),COLLEGE("大专",68),HSCHOOL("高中",34),ZS("中等专业学校或中等技术学校",34),JX("技术学校",34),ZZ("中等专业",47),CZ("初中",34),XX("小学",34),OTHER("其他",34);
    	private String college;
    	private int score;
    	UnmarriedEducationStatus(String college,int score) {
    		this.college=college;
    		this.score=score;
    	}
    	
		public String getCollege() {
			return college;
		}

		public void setCollege(String college) {
			this.college = college;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public static int getstatusScore(String college) {
			for (UnmarriedEducationStatus c : UnmarriedEducationStatus.values()) { 
				if(college.indexOf(c.getCollege())!=-1){
	                return c.score;  
	            } 
	        }  
	        return UnmarriedEducationStatus.OTHER.getScore();  
		}
	} 
    //已婚对应的分值
    enum MarriedEducationStatus {  
    	POSTGRADUATE("硕士",73),UNDERGRADUATE("本科",73),COLLEGE("大专",53),HSCHOOL("高中",47),ZZ("中等专业学校或中等技术学校",47),JX("技术学校",47),CZ("初中",47),XX("小学",34),OTHER("其他",34);
    	private String college;
    	private int score;
    	MarriedEducationStatus(String college,int score) {
    		this.college=college;
    		this.score=score;
    	}
    	
		public String getCollege() {
			return college;
		}

		public void setCollege(String college) {
			this.college = college;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public static int getstatusScore(String college) {
			for (MarriedEducationStatus c : MarriedEducationStatus.values()) {  
				if(college.indexOf(c.getCollege())!=-1){ 
	                return c.score;  
	            } 
	        }  
	        return MarriedEducationStatus.OTHER.getScore();  
		}
	} 
    //离婚对应的学历分值
    enum DivorceEducationStatus {  
    	POSTGRADUATE("硕士",53),UNDERGRADUATE("本科",53),COLLEGE("大专",53),HSCHOOL("高中",34),ZZ("中等专业学校或中等技术学校",34),JX("技术学校",34),CZ("初中",34),XX("小学",34),OTHER("其他",34);
    	private String college;
    	private int score;
    	DivorceEducationStatus(String college,int score) {
    		this.college=college;
    		this.score=score;
    	}
    	
		public String getCollege() {
			return college;
		}

		public void setCollege(String college) {
			this.college = college;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public  static int getstatusScore(String college) {
			for (DivorceEducationStatus c : DivorceEducationStatus.values()) {  
				if(college.indexOf(c.getCollege())!=-1){
	                return c.score;  
	            } 
	        }  
	        return DivorceEducationStatus.OTHER.getScore();  
		}
	} 
    //丧偶对应的学历分值
    enum WidowedEducationStatus {  
    	POSTGRADUATE("硕士",53),UNDERGRADUATE("本科",53),COLLEGE("大专",53),HSCHOOL("高中",34),ZZ("中等专业学校或中等技术学校",34),JX("技术学校",34),CZ("初中",34),XX("小学",34),OTHER("其他",34);
    	private String college;
    	private int score;
    	WidowedEducationStatus(String college,int score) {
    		this.college=college;
    		this.score=score;
    	}
    	
		public String getCollege() {
			return college;
		}

		public void setCollege(String college) {
			this.college = college;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public static int getstatusScore(String college) {
			for (WidowedEducationStatus c : WidowedEducationStatus.values()) {  
				if(college.indexOf(c.getCollege())!=-1){
	                return c.score;  
	            } 
	        }  
	        return WidowedEducationStatus.OTHER.getScore();  
		}
	} 
    //其他对应的学历分值
    enum OtherEducationStatus {  
    	POSTGRADUATE("硕士",53),UNDERGRADUATE("本科",53),COLLEGE("大专",53),HSCHOOL("高中",34),ZZ("中等专业学校或中等技术学校",34),JX("技术学校",34),CZ("初中",34),XX("小学",34),OTHER("其他",34);
    	private String college;
    	private int score;
    	OtherEducationStatus(String college,int score) {
    		this.college=college;
    		this.score=score;
    	}
    	
		public String getCollege() {
			return college;
		}

		public void setCollege(String college) {
			this.college = college;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public static int getstatusScore(String college) {
			for (OtherEducationStatus c : OtherEducationStatus.values()) {  
				if("".equals(college)||college==null) {
					break;
				}
				if(college.indexOf(c.getCollege())!=-1){
	                return c.score;  
	            } 
	        }  
	        return OtherEducationStatus.OTHER.getScore();  
		}
    }
}