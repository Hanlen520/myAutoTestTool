import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @Author:sh
 * @Description:
 * @DATE:Ceated in 10:56 2017/12/19
 */
public class Readproperties {
    Boolean errorStop;
    Boolean failStop;
    int watiElementSec;
    String excelPath;//测试数据excel存放路径
    String reportPath;//测试报告存放路径
    Boolean reportHtmlAddStamp;//生的测试报告是否加时间戳开关，true表示增加时间戳
    String driverPath;//浏览器驱动存放路径
    String ipPort;//测试环境访问协议、ip地址、端口
    String userName;//变量
    String passWord;//变量
    String sheetTOTest;//excel中需要进行测试的sheet页的名字
    InputStream in;

    public Readproperties() throws IOException {
        this.readpro();
    }

    public void readpro() throws IOException {
        Properties prop = new Properties();// 属性集合对象

        InputStream in = null;
        FileOutputStream oFile = null;
        in = new BufferedInputStream(new FileInputStream("src/prop.properties"));
        //prop.load(in);//直接这么写，如果properties文件中有汉子，则汉字会乱码。因为未设置编码格式。
        prop.load(new InputStreamReader(in, "gbk"));
        in.close();




//        FileInputStream fis = new FileInputStream("src/prop.properties");// 属性文件输入流
//        prop.load(fis);// 将属性文件流装载到Properties对象中
//        fis.close();// 关闭流
        //读取errorStop
        this.errorStop=Boolean.parseBoolean(prop.getProperty("errorStop","false"));
        System.out.println("从prop.properties中读取：errorStop=" + prop.getProperty("errorStop","false"));
        //读取failStop
        this.failStop=Boolean.parseBoolean(prop.getProperty("failStop","false"));
        System.out.println("从prop.properties中读取：failStop=" + prop.getProperty("failStop", "false"));
        //读取watiElementSec
        this.watiElementSec=Integer.parseInt(prop.getProperty("watiElementSec","10"));
        System.out.println("从prop.properties中读取：watiElementSec=" + prop.getProperty("watiElementSec", "10"));
        //读取excelPath
        this.excelPath=prop.getProperty("excelPath");
        System.out.println("从prop.properties中读取：excelPath=" + prop.getProperty("excelPath"));
        //读取reportPath
        this.reportPath=prop.getProperty("reportPath");
        System.out.println("从prop.properties中读取：reportPath=" + prop.getProperty("reportPath"));
        //读取reportHtmlAddStamp
        this.reportHtmlAddStamp=Boolean.parseBoolean(prop.getProperty("reportHtmlAddStamp"));
        //读取driverPath
        this.driverPath=prop.getProperty("driverPath","未填写");
        //读取ipPort
        this.ipPort=prop.getProperty("ipPort","未填写");
        //读取userName
        this.userName=prop.getProperty("userName","未填写");
        //读取passWord
        this.passWord=prop.getProperty("passWord","未填写");
        //读取excel中需要进行测试的sheet页的名字
        this.sheetTOTest=prop.getProperty("sheetTOTest","all");
    }

    public static void main(String[] args) throws IOException {
        Readproperties read=new Readproperties();
        System.out.println(read.reportPath);
        String time=new SimpleDateFormat("YYYY-M-d HH:mm:ss").format(new Date());
        System.out.println(read.reportPath.split("\\.")[0]+time+".html");
        System.out.println("drivePath:"+read.driverPath);
        System.out.println("ipPort:"+read.ipPort);
        System.out.println("userName:"+read.userName+"   passWord:"+read.passWord);
        System.out.println("sheetTOTest:"+read.sheetTOTest);


    }
}
