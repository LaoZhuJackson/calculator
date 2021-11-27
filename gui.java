package chop07.cal_3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static chop07.cal_3.method.*;

public class gui extends JFrame implements ActionListener {
    //用字符串数组存储按键名字
    private final String[] KEYS = {"7", "8", "9", "/", "sqrt", "4", "5", "6",
            "*", "%", "1", "2", "3", "-", "1/x", "0", "+/-", ".", "+", "="};
    //计算机的功能键显示名字
    private final String[] Command = {"C", "CE", "Backspace"};
    //左侧存储计算按键
    private final String[] M = {"❤", "MC", "MR", "M-", "M+"};
    //计算器按钮
    private final JButton[] keys = new JButton[KEYS.length];
    //功能按钮
    private final JButton[] commands = new JButton[Command.length];
    //左侧M按钮
    private final JButton[] m = new JButton[M.length];
    private final JLabel hisText = new JLabel("History:");
    private final JLabel memoryText = new JLabel("Memory:");
    //计算标签框
    private JLabel resultText = new JLabel("0");
    private JTextArea hisArea = new JTextArea("None...", 9, 5);
    private JTextArea memoryArea = new JTextArea("None...", 9, 5);
    //添加滚动条
    private JScrollPane his_jsp = new JScrollPane(hisArea);
    private JScrollPane mem_jsp = new JScrollPane(memoryArea);

    //标志是否是第一个数字
    private boolean firstDigit = true;
    //标志是否是一个完整的算式
    private boolean complete_formula = true;
    //用来判断该数字是否是第一个数字，因为一旦输入状态就会变为false
    //计算中间结果,temp1和temp2分别用于普通计算和M+M-
    private boolean temp_num_isEmpty = true;//检测temp_num时候存有数字
    //当前运算符
    private String operator = "none";
    //存储历史和记忆的动态数组
    private ArrayList<String> hisArrayList = new ArrayList<>(10);
    private String memoryNum = "None...";

    public gui() {
        super("laozhuCalculator");
        //初始化
        init();
        //设置背景颜色
        this.setBackground(Color.black);
        //设置关闭事件
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //设置居中
        this.setLocationRelativeTo(null);
        //不允许修改窗口大小
        this.setResizable(false);
        //使组件大小合适
        this.pack();
        //设置可见
        this.setVisible(true);
    }

    /**
     * 初始化计算器
     */
    private void init() {
        //设置允许换行
        hisArea.setLineWrap(true);
        memoryArea.setLineWrap(true);
        //设置不可编辑
        hisArea.setEditable(false);
        memoryArea.setEditable(false);
        //标签右对齐
        resultText.setHorizontalAlignment(JLabel.RIGHT);
        Font font = new Font("微软雅黑", Font.BOLD, 30);
        resultText.setFont(font);
        //设置滚动条只有在文本超出的时候显示
        his_jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mem_jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        /**
         * 计算面板
         */
        //数字键面板
        JPanel calcKeysPanel = new JPanel();
        calcKeysPanel.setLayout(new GridLayout(4, 5, 3, 3));
        for (int i = 0; i < KEYS.length; i++) {
            //创建按钮数组，通过循环名字数组命名
            keys[i] = new JButton(KEYS[i]);
            keys[i].addActionListener(this);
            //将按键添加到面板
            calcKeysPanel.add(keys[i]);
            //设置前景色
            keys[i].setForeground(Color.blue);
        }
        //将其中的运算符按键作特殊标红处理
        keys[3].setForeground(Color.red);
        keys[8].setForeground(Color.red);
        keys[13].setForeground(Color.red);
        keys[18].setForeground(Color.red);
        keys[19].setForeground(Color.red);

        //功能面板
        JPanel commandsPanel = new JPanel();
        //网格布局，一行三列
        commandsPanel.setLayout(new GridLayout(1, 3, 3, 3));
        for (int i = 0; i < Command.length; i++) {
            //创建按钮数组，通过循环名字数组命名
            commands[i] = new JButton(Command[i]);
            commands[i].addActionListener(this);
            //将按键添加到面板
            commandsPanel.add(commands[i]);
            //设置前景色
            commands[i].setForeground(Color.magenta);
        }

        //M面板
        JPanel calmsPanel = new JPanel();
        //网格布局，一列五行
        calmsPanel.setLayout(new GridLayout(5, 1, 3, 3));
        for (int i = 0; i < M.length; i++) {
            //创建按钮数组，通过循环名字数组命名
            m[i] = new JButton(M[i]);
            m[i].addActionListener(this);
            //将按键添加到面板
            calmsPanel.add(m[i]);
            //设置前景色
            m[i].setForeground(Color.magenta);
        }
        m[0].setForeground(Color.red);

        //计算面板
        JPanel calcPanel = new JPanel();
        calcPanel.setLayout(new BorderLayout(3, 3));
        calcPanel.add("North", commandsPanel);
        calcPanel.add("Center", calcKeysPanel);
        //标签面板
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        top.setBackground(Color.white);
        top.add("Center", resultText);

        /**
         * 计算器面板合体
         */
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout(3, 0));
        left.add("North", top);
        left.add("Center", calcPanel);
        left.add("West", calmsPanel);

        /**
         * 历史记忆面板 Grid
         */
        //历史面板
        JPanel his_memPanel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        his_memPanel.setLayout(gbl);
        //实例化约束管理器
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;//若组件所在区域大于组件本身则填充空位
        //设定hisText左上角坐标为（0,0）,占用格子为一行一列
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbl.setConstraints(hisText, gbc);
        //设定hisArea左上角坐标为（0,1）,占用格子为五行一列
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 5;
        gbl.setConstraints(hisArea, gbc);
        //设定memoryText左上角坐标为（0,1）,占用格子为一行一列
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbl.setConstraints(memoryText, gbc);
        //设定memoryArea左上角坐标为（1,1）,占用格子为五行一列
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 5;
        gbl.setConstraints(memoryArea, gbc);

        //添加进panel
        his_memPanel.add(hisText);
        his_memPanel.add(hisArea);
        his_memPanel.add(memoryText);
        his_memPanel.add(memoryArea);
        /**
         * 整体布局合体
         */
        //getContentPane()函数获取内容面板
        getContentPane().setLayout(new BorderLayout(3, 5));
        getContentPane().add("West", left);
        getContentPane().add("East", his_memPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //获取事件源标签
        String label = e.getActionCommand();
        if (label.equals(Command[0])) {
            handleC();
        } else if (label.equals(Command[1])) {
            handleCE();
        } else if (label.equals(Command[2])) {
            handleBackspace();
        } else if ("0123456789.".contains(label)) {   //indexOf() 返回指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1
            handleNumber(label);                    //boolean contains() 方法用于判断字符串中是否包含指定的字符或字符串
        } else {
            handleOperator(label);
        }
    }

    private void handleOperator(String label) {
        operator = label;
        switch (operator) {
            case "+":
            case "*":
            case "-":
            case "/":
                press_binary_operator(operator);
                break;
            case "1/x":
            case "+/-":
            case "sqrt":
                press_unary_operator(operator);
                break;
            case "M+":
                if (memoryNum.equals("None...")) {
                    memoryNum = String.valueOf(0 + Double.parseDouble(resultText.getText()));
                } else {
                    //每按一次更新一次记忆里面存储的数字
                    memoryNum = String.valueOf(Double.parseDouble(resultText.getText()) + Double.parseDouble(memoryNum));
                }
                //打印在memoryArea
                memoryArea.setText(memoryNum);
                //每按一遍操作符，下一次都是第一次输入数字
                firstDigit = true;
                break;
            case "M-":
                if (memoryNum.equals("None...")) {
                    memoryNum = String.valueOf(0 - Double.parseDouble(resultText.getText()));
                } else {
                    //每按一次更新一次记忆里面存储的数字
                    memoryNum = String.valueOf(Double.parseDouble(memoryNum) - Double.parseDouble(resultText.getText()));
                }
                //打印在memoryArea
                memoryArea.setText(memoryNum);
                //每按一遍操作符，下一次都是第一次输入数字
                firstDigit = true;
                break;
            case "MC":
                memoryNum = "None...";
                //打印在memoryArea
                memoryArea.setText(memoryNum);
                //每按一遍操作符，下一次都是第一次输入数字
                firstDigit = true;
                break;
            case "MR":
                resultText.setText(memoryNum);
                //清理暂存
                calculationArrayList_temp.clear();
                calculationArrayList.clear();
                //将答案存储起来，为下一次计算作准备
                calculationArrayList_temp.add(resultText.getText());
                calculationArrayList.add(resultText.getText());
                //每按一遍操作符，下一次都是第一次输入数字
                firstDigit = true;
                break;
            case "=":
                press_binary_operator(operator);
                String result = calculate_result();
                resultText.setText(result);
                //打印历史记录
                hisArrayList.add(getArrayText(calculationArrayList));
                hisArrayList.add("\n");//添加换行
                hisArea.setText(getArrayText(hisArrayList));
                //清理暂存
                calculationArrayList_temp.clear();
                calculationArrayList.clear();
                singleNum.clear();
                temp_num = temp_operator = "";
                //将答案存储起来，为下一次计算作准备
                calculationArrayList_temp.add(result);
                calculationArrayList.add(result);
//                complete_formula=true;//单条式子完成
                break;
        }
    }

    private void press_unary_operator(String operator) {
        //按了操作符说明之前的数字已经被确定，将数字添加进历史数组，此时calculationArrayList_temp不作变动
        calculationArrayList.add(resultText.getText());
        calculationArrayList.add(" " + operator + " ");
        String result = resultText.getText();
        switch (operator) {
            case "1/x" -> {
                result = String.valueOf(1.0 / Double.parseDouble(result));
                resultText.setText(result);
            }
            case "+/-" -> {
                result = String.valueOf((-1.0) * Double.parseDouble(result));
                resultText.setText(result);
            }
            case "sqrt" -> {
                result = String.valueOf(Math.sqrt(Double.parseDouble(result)));
                resultText.setText(result);
            }
        }
        calculationArrayList.add("=" + " " + resultText.getText());
        //打印历史记录
        hisArrayList.add(getArrayText(calculationArrayList));
        hisArrayList.add("\n");//添加换行
        hisArea.setText(getArrayText(hisArrayList));
        //清除缓存
        calculationArrayList.clear();
        //重置singleNum
        singleNum.clear();
        singleNum.add(resultText.getText());
    }

    private void press_binary_operator(String operator) {
        //按了操作符说明之前的数字已经被确定，将数字添加进历史数组，此时calculationArrayList_temp不作变动
        calculationArrayList.add(getArrayText(singleNum));
        calculationArrayList.add(" " + operator + " ");
        resultText.setText(resultText.getText() + operator);
        if (temp_num_isEmpty) {//如果暂存值为空
            if (operator.equals("*") || operator.equals("/")) {
                temp_num = getArrayText(singleNum);
                temp_operator = operator;//存下这个运算符
                temp_num_isEmpty = false;//标识存在待算的数
            } else {
                calculationArrayList_temp.add(getArrayText(singleNum));
                calculationArrayList_temp.add(" " + operator + " ");
                temp_num_isEmpty = true;
            }
        } else {//暂存值不为空
            double first_num = Double.parseDouble(temp_num);
            double second_num = Double.parseDouble(getArrayText(singleNum));
            String result = "0";
            if (temp_operator.equals("*")) {
                result = String.valueOf(first_num * second_num);
            } else if (temp_operator.equals("/")) {
                result = String.valueOf(first_num / second_num);
            }
            if (operator.equals("*") || operator.equals("/")) {//如果连乘或者连除
                temp_num = result;
                temp_operator = operator;
                temp_num_isEmpty = false;
            } else {//+或-或=的时候直接写入temp数组
                calculationArrayList_temp.add(result);//存入暂存数组而不是calculationArrayList
                calculationArrayList_temp.add(" " + operator + " ");
                temp_num_isEmpty = true;
            }
        }
        singleNum.clear();//读出后对数组进行清除
        firstDigit = true;
    }

    private void handleNumber(String num) {
        if (complete_formula && firstDigit) {//如果不是一个完整的算式并且是第一次输入数字
            resultText.setText(num);
            save_num(num);
        } else if ((num.equals(".")) && (!resultText.getText().contains("."))) {
            resultText.setText(resultText.getText() + ".");
            save_num(num);
        } else if (!num.equals(".")) {
            resultText.setText(resultText.getText() + num);
            save_num(num);
        }
        firstDigit = false;
        complete_formula = false;
    }


    private void handleBackspace() {
        String text=resultText.getText();
        int i =text.length();
        if (i>0){
            if (text.substring(text.length()-1).contains("*/")){//如果最后一位是*//运算符
                temp_num=null;
                temp_operator=null;
                temp_num_isEmpty=true;
                calculationArrayList= (ArrayList<String>) calculationArrayList.subList(0,calculationArrayList_temp.size()-1);
                text=text.substring(0,i-1);
            }else if (text.substring(text.length()-1).contains("+-")){
                calculationArrayList= (ArrayList<String>) calculationArrayList.subList(0,calculationArrayList_temp.size()-1);
                calculationArrayList_temp= (ArrayList<String>) calculationArrayList_temp.subList(0,calculationArrayList_temp.size()-1);
                text=text.substring(0,i-1);
            }else{//为数字
                text=text.substring(0,i-1);
            }
            if (text.length()==0){
                handleCE();
            }else{
                resultText.setText(text);
            }
        }
    }

    private void handleCE() {
        resultText.setText("0");
        firstDigit=true;
        operator="none";
        calculationArrayList_temp.clear();
        calculationArrayList.clear();
    }

    private void handleC() {
        handleCE();
        hisArea.setText("None...");
        memoryArea.setText("None...");
    }
}
