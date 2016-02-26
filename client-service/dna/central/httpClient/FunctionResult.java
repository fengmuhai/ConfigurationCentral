package dna.central.httpClient;

/**
* 函数返回结果类
*/
/**
* 函数返回结果
*/
public class FunctionResult {

  /**
   * 返回类型
   * 1--成功
   * 0--失败
   */
  private Integer returnType;
  /**
   * 返回值
   */
  private Object returnValue;
  /**
   * 业务数据2
   */
  private Object dataValue;
  /**
   * 业务数据2
   */
  private Object dataValue2;
  private String str1="";
  private String str2="";
  private String str3="";
  private String str4="";
  private String str5="";//临时变量
  private String str6="";//反洗钱标识
  private String str7="";//要求身份证
  private String str8="";//要求姓名
  private String str9="";//要求交易手机号
  
  

  private boolean validFlag = false;     //是否二次验证过
  

 
  /**
   * 注释
   */
  private String remark;

  /**
   */
  public FunctionResult() {
  }

  public FunctionResult(Integer type, Object value, String remark) {
      this.setReturnType(type);
      this.setReturnValue(value);
      this.setRemark(remark);

  }

  public Object getDataValue2() {
      return dataValue2;
  }

  public void setDataValue2(Object temp) {
      dataValue2 = temp;
  }

  public Object getDataValue() {
      return dataValue;
  }

  public void setDataValue(Object temp) {
      dataValue = temp;
  }

  /**
   * Access method for the returnType property.
   *
   * @return   the current value of the returnType property
   */
  public Integer getReturnType() {
      return returnType;
  }

  /**
   * Sets the value of the returnType property.
   *
   * @param aReturnType the new value of the returnType property
   */
  public void setReturnType(Integer temp) {
      returnType = temp;
  }

  /**
   * Access method for the returnValue property.
   *
   * @return   the current value of the returnValue property
   */
  public Object getReturnValue() {
      return returnValue;
  }

  /**
   * Sets the value of the returnValue property.
   *
   * @param aReturnValue the new value of the returnValue property
   */
  public void setReturnValue(Object temp) {
      returnValue = temp;
  }

  /**
   * Access method for the remark property.
   *
   * @return   the current value of the remark property
   */
  public String getRemark() {
      return remark;
  }

  /**
   * Sets the value of the remark property.
   *
   * @param aRemark the new value of the remark property
   */
  public void setRemark(String temp) {
      remark = temp;
  }
  private boolean checkCustomerBeforePay = false;

  @Override
  public String toString() {
      return returnType + "|" + remark + "|" + returnValue + "|" + dataValue + "|" + dataValue2 + "|" + gcOrder;
  }

  public boolean getCheckCustomerBeforePay() {
      return checkCustomerBeforePay;
  }

  public void setCheckCustomerBeforePay(boolean checkCustomerBeforePay) {
      this.checkCustomerBeforePay = checkCustomerBeforePay;
  }
  private boolean gcOrder = true;

  public boolean isGcOrder() {
      return gcOrder;
  }

  public void setGcOrder(boolean gcOrder) {
      this.gcOrder = gcOrder;
  }

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public String getStr3() {
		return str3;
	}

	public void setStr3(String str3) {
		this.str3 = str3;
	}

	public String getStr4() {
		return str4;
	}

	public void setStr4(String str4) {
		this.str4 = str4;
	}

	public String getStr5() {
		return str5;
	}

	public void setStr5(String str5) {
		this.str5 = str5;
	}

	public String getStr6() {
		return str6;
	}

	public void setStr6(String str6) {
		this.str6 = str6;
	}

	public String getStr7() {
		return str7;
	}

	public void setStr7(String str7) {
		this.str7 = str7;
	}

	public String getStr8() {
		return str8;
	}

	public void setStr8(String str8) {
		this.str8 = str8;
	}

	public String getStr9() {
		return str9;
	}

	public void setStr9(String str9) {
		this.str9 = str9;
	}

	
	public boolean isValidFlag() {
		return validFlag;
	}

	public void setValidFlag(boolean validFlag) {
		this.validFlag = validFlag;
	}
}