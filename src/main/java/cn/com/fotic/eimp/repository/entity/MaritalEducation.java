package cn.com.fotic.eimp.repository.entity;
/**
 * 婚姻学历权重分
 * @author yangll
 */
public class MaritalEducation { 
	
	public static int getMaritalEducationScore(BankCredit bankCredit){
		if(MaritalStatus.UNMARRIED.getMaritalStatus().equals(bankCredit.getRateMaritalState())) {
			System.out.println("ssssssss"+UnmarriedEducationStatus.getstatusScore(bankCredit.getRateEduLevel()));
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
     enum  UnmarriedEducationStatus{  
    	POSTGRADUATE("硕士",105),UNDERGRADUATE("本科",105),COLLEGE("大专",68),HSCHOOL("高中",34),JX("技校",34),ZZ("中专",47),CZ("初中",34),XX("小学",34),OTHER("其他",34);
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
    
    enum MarriedEducationStatus {  
    	POSTGRADUATE("硕士",73),UNDERGRADUATE("本科",73),COLLEGE("大专",53),HSCHOOL("高中",47),JX("技校",47),ZZ("中专",47),CZ("初中",47),XX("小学",34),OTHER("其他",34);
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
    
    enum DivorceEducationStatus {  
    	POSTGRADUATE("硕士",53),UNDERGRADUATE("本科",53),COLLEGE("大专",53),HSCHOOL("高中",34),JX("技校",34),ZZ("中专",34),CZ("初中",34),XX("小学",34),OTHER("其他",34);
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
    
    enum WidowedEducationStatus {  
    	POSTGRADUATE("硕士",53),UNDERGRADUATE("本科",53),COLLEGE("大专",53),HSCHOOL("高中",34),JX("技校",34),ZZ("中专",34),CZ("初中",34),XX("小学",34),OTHER("其他",34);
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
    
    enum OtherEducationStatus {  
    	POSTGRADUATE("硕士",53),UNDERGRADUATE("本科",53),COLLEGE("大专",53),HSCHOOL("高中",34),JX("技校",34),ZZ("中专",34),CZ("初中",34),XX("小学",34),OTHER("其他",34);
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
				if(college.indexOf(c.getCollege())!=-1){
	                return c.score;  
	            } 
	        }  
	        return OtherEducationStatus.OTHER.getScore();  
		}
    }
}