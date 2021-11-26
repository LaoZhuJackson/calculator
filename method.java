package chop07.cal_3;

import java.util.ArrayList;

public class method {
    public static ArrayList<String> singleNum = new ArrayList<>(5);//用于存放单个数字
    public static String temp_num;
    public static String temp_operator;
    public static ArrayList<String> calculationArrayList_temp = new ArrayList<>(5);//用于存放经过优先级计算的式子
    public static ArrayList<String> calculationArrayList = new ArrayList<>(5);//用于读出原来的式子
    //获取数组里面的算式或数字
    public static String getArrayText(ArrayList<String> calculationList){
        StringBuffer tempStr=new StringBuffer();
        for (String s:calculationList){
            tempStr.append(s);
        }
        return String.valueOf(tempStr);
    }
    //用于存放单个数字
    public static void save_num(String num){
        singleNum.add(num);
    }
    //进行最后等于的加减运算
    public static String calculate_result(){
        double sum=0.0;
        System.out.println("calculationArrayList_temp:"+getArrayText(calculationArrayList_temp));
        double temp_num = 0.0;
        boolean next_num_is_negative=false;
        for (String s:calculationArrayList_temp){
            System.out.println("当前s.trim()的值为："+s.trim());
            if (isNum(s)){
                if (next_num_is_negative){//该数的上一个操作符是负数
                    temp_num=(-1.0)*Double.parseDouble(s);
                }else {//该数的上一个操作符是正数
                    temp_num=Double.parseDouble(s);
                }
            }else{//s为操作符或者为空
                if (s.trim().equals(""))//空值的时候跳过
                    continue;
                next_num_is_negative= s.trim().equals("-");
                sum+=temp_num;
            }
        }
        //让整数不显示小数点，让小数正常显示小数点
        long t1;
        double t2;
        t1 = (long) sum;
        t2 = sum - t1;
        if (t2 == 0) {
            calculationArrayList.add(String.valueOf(t1));
            return String.valueOf(t1);

        } else {
            calculationArrayList.add(String.valueOf(sum));
            return String.valueOf(sum);
        }
    }
    //通过try catch实现判断是否为数字
    public static boolean isNum(String s) {
        try {
            Double.parseDouble(s);
        }catch (RuntimeException e){
            return false;
        }
        return true;
    }
}
