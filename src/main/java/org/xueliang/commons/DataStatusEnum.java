package org.xueliang.commons;


/**
 * 数据状态枚举信息
 * 将常用的几种数据状态独立出来，方便维护
 * @author XueLiang
 *
 */
public enum DataStatusEnum {
	NORMAL((byte)0),		//大于等于0为正常状态
	DISABLED((byte)-1),	//等于-1为禁用状态
	DELETED((byte)-2);	//等于-2为已删除状态
	
	private final byte value;
	
	DataStatusEnum(byte value){
		this.value = value;
	}
	
	/**
	 * 获取相应状态对应的数值
	 * @return
	 */
	public final int getValue() {
		return this.value;
	}
	
	public final byte getByteValue() {
        return this.value;
    }
}
