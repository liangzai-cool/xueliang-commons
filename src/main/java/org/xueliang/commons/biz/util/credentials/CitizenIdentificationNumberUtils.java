package org.xueliang.commons.biz.util.credentials;

/**
 * @use 15位身份证升级、校验
 * @ProjectName stuff
 * @Author <a href="mailto:mhmyqn@qq.com">mumaoqiang</a></br>
 * @Date 2011-5-13 下午09:53:09 </br>
 * @FullName com.mmq.VerifyIDCardNumber.java </br>
 * @JDK 1.6.0 </br>
 * @Version 1.0 </br>
 */
public class CitizenIdentificationNumberUtils {

    /**
     * 根据15位的身份证号码获得18位身份证号码
     *
     * @param fifteenIDCard
     *            15位的身份证号码
     * @return 升级后的18位身份证号码
     * @throws Exception
     *             如果不是15位的身份证号码，则抛出异常
     */
    public static String getEighteenIDCard(String fifteenIDCard) {
        if (fifteenIDCard != null && fifteenIDCard.length() == 15) {
            StringBuilder sb = new StringBuilder();
            sb.append(fifteenIDCard.substring(0, 6)).append("19").append(fifteenIDCard.substring(6));
            sb.append(getVerifyCode(sb.toString()));
            return sb.toString();
        }
        throw new IllegalArgumentException("不是15位的身份证[" + fifteenIDCard + "]");
    }

    /**
     * 获取校验码
     *
     * @param idCardNumber
     *            不带校验位的身份证号码（17位）
     * @return 校验码
     * @throws Exception
     *             如果身份证没有加上19，则抛出异常
     */
    public static char getVerifyCode(String idCardNumber) {
        if (idCardNumber == null || idCardNumber.length() < 17) {
            throw new IllegalArgumentException("不合法的身份证号码[" + idCardNumber + "]");
        }
        char[] A = idCardNumber.toCharArray();
        int[] W = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
        int S = 0;
        for (int i = 0; i < W.length; i++) {
            S += (A[i] - '0') * W[i];
        }
        int a1 = (12 - S % 11) % 11;
        if (a1 == 10) {
            return 'X';
        }
        return Character.forDigit(a1, 10);
    }

    /**
     * 校验18位的身份证号码的校验位是否正确
     *
     * @param idCardNumber
     *            18位的身份证号码
     * @return
     * @throws Exception
     */
    public static boolean verify(String idCardNumber) {
        if (idCardNumber == null || idCardNumber.length() != 18) {
            throw new IllegalArgumentException("不是18位的身份证号码[" + idCardNumber + "]");
        }
        return getVerifyCode(idCardNumber) == idCardNumber.charAt(idCardNumber.length() - 1);
    }
    
    public static void main(String[] args) {
        System.out.println(verify("42110219920116561X"));
    }
}
