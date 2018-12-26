//import jdk.jfr.internal.instrument.ThrowableTracer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
//import static org.fusesource.jansi.Ansi.*;
//import static org.fusesource.jansi.Ansi.Color.*;


public class TestDemo {
    WebDriver driver;
    ReadExcel readExcel;
    ReadExcel readAllExcel;
    Map<String, List<String>> sheetMap;//单行测试数据组成list结构
    List<List<List<String>>> excelList;//测试数据组成list结构
    Boolean failStop;//true，执行用例定位失败时，中断执行；false，执行用例定位失败时，不中断
    Boolean errorStop;
    int watiElementSec;//等待页面元素出现时间秒

    int totalCase=0;//总共用例数
    int passedCase=0;//通过用例数
    int errorCase=0;//定位失败用例数
    int asserFailCase=0;//断言异常用例数
    List<String> testCaseResultList;//存储单条用例的执行结果
    public List<List<String>> reportInfo;//存储所有用例结果
    String startTime;//测试开始时间
    String endTime;//测试结束时间
    boolean reportHtmlAddStamp;//测试报告文件增加时间戳开关
    String excelPath;//测试数据路径
    String reportPath;//测试报告存放路径
    String driverPath;//浏览器驱动存放路径
    String ipPort;//测试环境访问协议、ip地址、端口
    String userName;//变量
    String passWord;//变量
    String sheetTOTest;//excel中需要进行测试的sheet页的名字

    //    String sheetName="人员表";
    List<String> allHandlesList;
    int testStepNum=0;//测试用例执行到第几步
    String employeeNumInhtml=null;//使用场景：在新增企业员工时，页面自动生成员工编号，employeeNumInhtml

    @BeforeTest
    public void setUp() throws IOException {
        System.out.println("开始测试");
        this.startTime=new SimpleDateFormat("YYYY-M-d HH:mm:ss").format(new Date());
        System.out.println("开始时间："+this.startTime);
        //读取配置文件，并赋值
        Readproperties readproperties=new Readproperties();
        this.failStop=readproperties.failStop;
        this.errorStop=readproperties.errorStop;
        this.excelPath=readproperties.excelPath;
        this.driverPath=readproperties.driverPath;
        this.ipPort=readproperties.ipPort;
        this.userName=readproperties.userName;
        this.passWord=readproperties.passWord;
        this.sheetTOTest=readproperties.sheetTOTest;


        this.reportHtmlAddStamp=readproperties.reportHtmlAddStamp;
        if(this.reportHtmlAddStamp){//reportHtmlAddStamp开关true，则给测试报告文件名增加时间戳
            System.out.println(readproperties.reportPath);
            this.reportPath=readproperties.reportPath.split("\\.")[0]+startTime.replaceAll(":","-")+".html";
        }else{//reportHtmlAddStamp开关false，测试报告文件名不增加时间戳
            this.reportPath=readproperties.reportPath;
        }
        allHandlesList=new ArrayList<String>();//所有窗口列表，切换窗口时使用


        watiElementSec=readproperties.watiElementSec;//页面出现元素的最大等时间
//        readExcel=new ReadExcel(excelPath,sheetName);//读取sheet页
//        sheetMap=readExcel.getSheetMap();//获得sheet页的map对象

        readAllExcel=new ReadExcel(excelPath);
        excelList=readAllExcel.getExcelList(sheetTOTest);//获取整个excek的list对象

        reportInfo=new ArrayList<List<String>>();//存储执行用例结果

        System.setProperty("webdriver.chrome.driver", driverPath);//webdriver驱动存放路径
        driver=new ChromeDriver();//初始化webdriver
        driver.manage().window().maximize();
    }
    @AfterTest
    public void tearDown() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
        System.out.println("测试结束");
    }

    //测试用例
    @Test(enabled = true)
    public void testTest() throws InterruptedException, IOException {
//        formSubmit(sheetMap.get("业务1_start"),sheetMap.get("用例1"));
//        formSubmits("业务1");
//        formSubmits("业务2");
//        formSubmits("业务3");
        allTest(this.errorStop,this.failStop);
//        System.out.println(reportInfo.toString());
//        System.out.println(reportInfo.size());

    }


    public void allTest(Boolean errorStop,Boolean failStop) throws InterruptedException {
        /**
         * @Author:SH`
         * @Date:10:31 2017/12/15
         * @Description:作用：执行excel中的全部用例
         * @Description:根据ReadExcel中getExcelList方法返回的list，循环遍历每行，根据每行的不同情况来实行用例
         * @Description:执行用的具体逻辑，见formSubmit（）
         * @params  * @param errorStop true时，执行用例定位失败时，中断执行；false时，执行用例定位失败时，不中断
         * @param failStop true时，执行用例断言失败时，中断执行；false时，执行用例断言失败时，不中断
         */
        this.failStop=failStop;
        boolean haveException=false;
        for(int i=0;i<excelList.size();i++){//第一层循环，获取List<List<List<String>>>中的sheet层的list
//            System.out.println("sheet 层级中的index为"+i+"的list："+excelList.get(i));
            for(int n=0;n<excelList.get(i).size();n++){
//                System.out.println("sheet 层级中的index为"+i+"  行层级中index为 "+n+" 的list："+excelList.get(i).get(n));
                if(excelList.get(i).get(n).get(0).contains("_start")){//遍历到含“_start”行时
                    List<String> startList=excelList.get(i).get(n);//操作行
                    List<String> testStepNameList=excelList.get(i).get(n-1);//测试步骤名称
                    while(true){
                        if(excelList.get(i).get(n+1).get(0).contains("操作步骤")){
                            testStepNameList.clear();
                            break;
                        }
                        if(errorStop==true){
                            totalCase++;
                            formSubmit(startList,excelList.get(i).get(n+1));//执行用例
                            passedCase++;
                        }else{
                            try {
                                totalCase++;
                                formSubmit(startList,excelList.get(i).get(n+1));//执行用例
                                testCaseResultList.add("通过");
                                reportInfo.add(testCaseResultList);
                                passedCase++;
                            }catch (Exception e){
                                haveException=true;
                                System.out.println("执行用例："+excelList.get(i).get(n+1).get(0)+" 出错，异常信息如下：");
                                errorCase++;
                                //e.printStackTrace();
                                System.out.println("错误信息： "+e.getMessage());
                                testCaseResultList.add("定位失败");
                                testCaseResultList.add("该用例中第"+testStepNum+"步出现异常,步骤名称："+testStepNameList.get(testStepNum).replaceAll("\"","\'")+
                                        "<br>异常信息如下：<br>"+e.getMessage().replaceAll("\"","\'")
                                        .replaceAll("<","《").replaceAll(">","》"));//用例报错信息加入到testCaseResultList
                                reportInfo.add(testCaseResultList);
                            }

//                            for(int r=0;r<3;r++){
//                                if (haveException && true){
//                                    try{
//                                        formSubmit(startList,excelList.get(i).get(n+1));//执行用例
//
//                                        testCaseResultList.add("通过");
//                                        reportInfo.add(testCaseResultList);
//                                        passedCase++;
//                                        errorCase++;
//                                        break;
//                                    }
//                                    catch (Exception repeatE){
//                                        System.out.println("执行用例："+excelList.get(i).get(n+1).get(0)+" 出错，异常信息如下：");
//                                        repeatE.printStackTrace();
//                                        reportInfo.remove(reportInfo.size()-1);
//                                        testCaseResultList.add("定位失败");
//                                        testCaseResultList.add("该用例中第"+testStepNum+"步出现异常,步骤名称："+excelList.get(i).get(n-1).get(testStepNum).replaceAll("\"","\'")+
//                                                "<br>异常信息如下：<br>"+repeatE.getMessage().replaceAll("\"","\'")
//                                                .replaceAll("<","《").replaceAll(">","》"));//用例报错信息加入到testCaseResultList
//                                        reportInfo.add(testCaseResultList);
//                                    }
//                                }
//                            }
                        }
//                            System.out.println("本次测试的html指标list："+excelList.get(i).get(n));
//                            System.out.println("本次测试的测试数据list："+excelList.get(i).get(n+1));
                        n++;
                        if(n==excelList.get(i).size()-1){//当读到sheet页的最后一行时，跳出循环
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("总共执行用例数："+totalCase);
        System.out.println("测试通过用例数："+passedCase);
        System.out.println("抛异常用例数："+errorCase);
        System.out.println("断言失败用例数："+asserFailCase);
        //生成结束时间
        this.endTime=new SimpleDateFormat("YYYY-M-d HH:mm:ss").format(new Date());
        System.out.println("测试结束时间："+this.endTime);
        //生成测试报告
        new Report().creatReport(reportInfo,totalCase,passedCase,errorCase,asserFailCase,reportPath,this.startTime,this.endTime,reportHtmlAddStamp);
    }

    public void formSubmit(List<String> htmlList,List<String> testDataList) throws InterruptedException {
        /**
         * @Author:SH
         * @Date:10:51 2017/12/15
         * @Description:作用：执行一条测试用例
         * @params  * @param htmlList excel表中定位行,即某个含"_start"行中所有字段集合
         * @param testDataList excel表中测试数据某行字段集合
         */
        testCaseResultList=new ArrayList<String>();//存储本次用例中定位及页面操作的执行结果信息
        testCaseResultList.add(testDataList.get(0));//用例名称加入到testCaseResultList

        System.out.println("############################开始执行用例："+testDataList.get(0)+"#################################");
//        System.out.println( ansi().eraseScreen().render("@|green 开始执行用例："+testDataList.get(0)+"|@") );

        for (int i=0;i<htmlList.size();i++){
            testStepNum=i;//执行测试用例到第几步
//            System.out.println("第"+testStepNum+"步:");
            System.out.print("第"+testStepNum+"步:");
            testStepNum=i;
            if(!(htmlList.get(0).contains("_start"))){
                System.out.println("执行测试用例过程中，方法formSubmit()传入的参数不是_start行");
                return;
            }
            //operatKey ：每一个步骤所选择的操作方法，起标志作用
            String operatKey=htmlList.get(i).split("=>")[htmlList.get(i).split("=>").length-1];
            //打开url
            // 操作行：url=>true
            // 数据行：要打开的网址
            if(operatKey.equals("url")){
                String url=testDataList.get(i);
                boolean rightUrl=false;
                if(url.contains("http") || url.contains("https")){
                    rightUrl=true;
                    System.out.println("使用excel中填写的数据打开url:"+url);
                    driver.get(url);
                }else if(url.startsWith("/")){
                    rightUrl=true;
                    url=ipPort+url;
                    System.out.println("使用prop.properties文件中配置的环境打开url:"+url);
                    driver.get(url);
                }
                if(!rightUrl){
                    System.out.println("注意：url没有配置正确，请维护excel或者prop.properties");
                }
            }
            //键盘输入。
            // 操作行：id=>1=>sendkeys
            // 数据行：要输入的内容
            if(operatKey.equals("sendkeys")) {
                Thread.sleep(500);
                String text = testDataList.get(i);
                if (text.equals("userName")) {
                    getElement(htmlList.get(i)).clear();
                    getElement(htmlList.get(i)).sendKeys(userName);
                } else if (text.equals("passWord")) {
                    getElement(htmlList.get(i)).clear();
                    getElement(htmlList.get(i)).sendKeys(passWord);
                } else {
                    getElement(htmlList.get(i)).clear();
                    getElement(htmlList.get(i)).sendKeys(text);
                    System.out.println("输入：" + text);
                }
            }
            //无id的input框输入
            // 操作行：员工名称=>inputNoIdSendkeys
            // 数据行：要输入的内容
            //使用场景：当input框不能根据id或name定位时
            else  if(operatKey.equals("inputNoIdSendkeys")){
                String sendKeystext=testDataList.get(i);
                String inputName=htmlList.get(i).split("=>")[0];
                //根据该input框前面的名称（标签对之间的text文本），通过层级定位到input框
                getElement("xpath=>//span[contains(text(),'"+inputName+"')]/..//input").sendKeys(sendKeystext);
                System.out.println(inputName+"-输入框输入："+sendKeystext);
            }

            //富文本框输入
            // 操作行：xpaths=>//textarea[@type='textarea']=>../../../span=>配建、代建及其他规划要求=>sendkeysTextArea
            //“//textarea[@type='textarea']”表示富文本框的list，“../../../span”表示该富文本框的名称，“配建、代建及其他规划要求”表示要定位的富文本框名称
            //“sendkeysTextArea”标志作用
            // 数据行：要输入的内容
            else if(operatKey.equals("sendkeysTextArea")){
                List<WebElement> textAreaList=getElements(htmlList.get(i));
//                System.out.println("testAreaList size:"+textAreaList.size());
                for(int s=0;s<textAreaList.size();s++){
                    String textAreaNameInHtml=textAreaList.get(s).findElement(By.xpath(htmlList.get(i).split("=>")[2])).getText();
//                    System.out.println(textAreaNameInHtml);
                    String textAreaNameInOperateRow=htmlList.get(i).split("=>")[3];
                    if(textAreaNameInHtml.equals(textAreaNameInOperateRow)){
                        textAreaList.get(s).sendKeys(testDataList.get(i));
                        System.out.println("富文本：\""+textAreaNameInHtml+"\"输入："+testDataList.get(i));
                        break;
                    }
                }
            }
            //点击按钮
            // 操作行：id=>btn1=>click
            // 数据行：boolean
            else if(operatKey.equals("click") && testDataList.get(i).toLowerCase().contains("true")){
                Thread.sleep(200);
                getElement(htmlList.get(i)).click();
                System.out.println("点击："+htmlList.get(i));
            }
            //点击select的按钮，并选中其中选项.
            // 操作行：id=>selectid=>select选项
            // 数据行：下拉框中的value值
            else if (operatKey.equals("select选项")){
                //定位到<select>,把WebElement对象作为参数传给，Select对象，在使用selectByValeu方法实现选择下拉框内容
                new Select(getElement(htmlList.get(i))).selectByValue(testDataList.get(i));
            }
            //进入iframe框
            // 操作行：id=>iframe=>进入iframe
            // 数据行：boolean
            else if(operatKey.equals("进入iframe")&&(testDataList.get(i).toLowerCase().contains("true"))){
                driver.switchTo().frame(getElement(htmlList.get(i)));
                System.out.println("进入iframe："+htmlList.get(i));
            }
            //退出iframe框
            // 操作行：返回到上级iframe
            // 数据行：boolean
            else if((htmlList.get(i).contains("返回到上级iframe"))&&(testDataList.get(i).toLowerCase().contains("true"))){
                System.out.println(driver.switchTo().parentFrame().toString());
                driver.switchTo().parentFrame();
            }
            //鼠标悬停
            // 操作行：xpath=>//div[@id='u1']/a[8]=>鼠标悬停
            // 数据行：boolean
            else if(htmlList.get(i).contains("鼠标悬停")&&(testDataList.get(i).toLowerCase().contains("true"))){
                Actions action = new Actions(driver);
                action.clickAndHold(getElement(htmlList.get(i))).perform();
                Thread.sleep(2000);
                System.out.println("悬停2s");
            }
            //模拟敲回车键
            // 操作行：xpath=>//input[@placeholder='按员工名称、员工编号、用户帐号、公司名称、部门名称进行搜索']=>enter
            // 数据行：boolean
            else if(operatKey.equals("enter")){
                getElement(htmlList.get(i)).sendKeys(Keys.ENTER);
                System.out.println("按回车键");
                Thread.sleep(2500);
            }
            //拖拽滚动条
            // 操作行：xpath=>//div[@class='form-collapses-item'][3]=>moveToElement
            // 数据行：boolean
            else if(operatKey.equals("moveToElement")){
                new Actions(driver).moveToElement(getElement(htmlList.get(i))).build().perform();
                System.out.println("moveToElement："+htmlList.get(i).split("=>")[1]);
            }
            //切换到指定窗口
            // 操作行：切换到新窗口=>3 “3”表示第三个出现的窗口
            // 数据行：boolean
            else if(operatKey.equals("切换到新窗口")&&(testDataList.get(i).toLowerCase().contains("true"))){
                goToWindow(htmlList.get(i).split("=>")[1]);
            }
            //关闭指定窗口
            // 操作行：关闭指定窗口
            // 数据行：2     关闭第二个出现的窗口
            else if (operatKey.equals("关闭指定窗口")){
                String nowInhandle=driver.getWindowHandle();
                driver.switchTo().window(allHandlesList.get(Integer.parseInt(testDataList.get(i))-1));
                driver.close();
                driver.switchTo().window(nowInhandle);
                allHandlesList.remove(allHandlesList.get(Integer.parseInt(testDataList.get(i))-1));
            }
            //关闭当前窗口
            // 操作行：关闭当前窗口
            // 数据行：boolean
            else if(operatKey.equals("关闭当前窗口")&&(testDataList.get(i).toLowerCase().contains("true"))){
                allHandlesList.remove(driver.getWindowHandle());
                driver.close();
            }
            //关闭主窗口外的所有窗口
            // 操作行：关闭主窗口外的所有窗口
            // 数据行：boolean
            else if (operatKey.equals("关闭主窗口外的所有窗口")&&(testDataList.get(i).toLowerCase().contains("true"))){
//                System.out.println("全部窗口"+allHandlesList.toString());
                for(int j=0;j<allHandlesList.size();){
                    if(j==0){
                        j++;
                    }else{
                        driver.switchTo().window(allHandlesList.get(j));
//                        System.out.println("关闭"+allHandlesList.get(j));
                        allHandlesList.remove(driver.getWindowHandle());
                        driver.close();
                        Thread.sleep(1000);
                    }
                }
                driver.switchTo().window(allHandlesList.get(0));
            }
            //设置线程等待时间
            // 操作行：等待毫秒
            // 数据行：毫秒值
            else if (htmlList.get(i).contains("等待毫秒")){
                int ms=Integer.parseInt(testDataList.get(i).split("\\.")[0]);
                Thread.sleep(ms);
                System.out.println("等待毫秒:"+ms);
            }
            //执行js
            //操作行：javaScript=>document.getElementsByClassName('footer')[0].style.display='none';
            //数据行：boolean
            else if(htmlList.get(i).contains("javaScript")&&(testDataList.get(i).toLowerCase().equals("true"))){
                String js = htmlList.get(i).split("=>")[1];
                ((JavascriptExecutor)driver).executeScript(js);
//                System.out.println(((JavascriptExecutor)driver).executeScript(js));
                String result=(String) ((JavascriptExecutor)driver).executeScript(js);
                System.out.println("获取js结果："+result);
                System.out.println("执行js");
            }
            //定位一组元素，并点击指定的text元素
            // 操作行：xpaths=>//span[@class='tree-node-label']=>goToText=>企业员工=>click=>elements
            //goToText表示通过获取标签对之间的text值来定位到元素 企业员工就是text的文本值
            // 数据行：boolean
            else if(htmlList.get(i).contains("=>Click=>elements")&&(htmlList.get(i).contains("goToText"))&&(testDataList.get(i).toLowerCase().equals("true"))){
                Thread.sleep(500);
                List<WebElement> elementsList=getElements(htmlList.get(i));
                for(int h=0;h<elementsList.size();h++){
                    String textInHtml=elementsList.get(h).getText();
//                    System.out.println(textInHtml);
                    if(textInHtml.equals(htmlList.get(i).split("=>")[3])){
                        elementsList.get(h).click();
                        System.out.println("点击"+elementsList.get(h).toString());
                        break;
                    }
                }
            }

            //一下分支作用：在系统管理-企业员工中，动态删除企业员工
            // 操作行：xpaths=>//div[@class='el-table__body-wrapper']/table/tbody/tr/td[2]/div/a
//            =>//div[@class='el-table__fixed-body-wrapper']/table/tbody/tr/td[10]/div/a[2]=>deleteStaff
            //“=>deleteStaff”表示进入该分支，xpaths路径为获取所有员工姓名的元素list
            // 数据行：要删除的员工姓名
            //说明：获取所有姓名，找到要删除的姓名，点击删除按钮
            else if(htmlList.get(i).contains("=>deleteStaff")){
                Thread.sleep(3000);
                List<WebElement> nameList=getElements(htmlList.get(i));
                List<WebElement> deleteButtonList=getElements("xpaths=>"+htmlList.get(i).split("=>")[2]);

                boolean hasTraversaed=false;
                for(int h=0;h<nameList.size();h++){
//                    System.out.println(nameList.get(h).getText());
                    if(nameList.get(h).getText().equals(testDataList.get(i))){
//                        System.out.println(deleteButtonList.get(h).toString());
                        deleteButtonList.get((h)).click();
                        Thread.sleep(500);
                        System.out.println("点击删除。"+"被删除姓名："+nameList.get(h).getText());
                        hasTraversaed=true;
                        break;
                    }
                }
                if(!hasTraversaed){
                    System.out.println("注意：在页面没有找到名字："+testDataList.get(i)+",因此并没有点击删除按钮");
                }
            }
            //下面分支内容作用：给指定的时间空间选择日期-day
            // 操作行：xpaths=>//input[@placeholder='选择日期']=>生日=>calendar
            //“生日”表示该时间对应的text
            // 数据行：2001-08-08
            //说明：需要先定位到输入时间控件的html元素，加入到list，找到其中可见元素进行操作
            else if(operatKey.equals("calendar")){
                Thread.sleep(1000);
                List<WebElement> elementList= getElements(htmlList.get(i));//点击元素，可出现时间控件的元素list
                for(int k=0;k<elementList.size();k++){
                    String text=elementList.get(k).findElement(By.xpath("../../../span")).getText();
                    if(text.equals(htmlList.get(i).split("=>")[2])){
                        elementList.get(k).click();//点击日期控件，调出选择框
                        Thread.sleep(500);
                        break;
                    }
                };
                List<WebElement> calendarContraler=getElements("xpaths=>//div[@class='el-picker-panel el-date-picker el-popper']");//时间控件的html结构的list，只有一个是display的
                for (int y=0;y<calendarContraler.size();y++){//遍历到display的html结构
                    if(calendarContraler.get(y).isDisplayed()){
                        boolean flagYear=true;//控制调节年按钮，当页面出现测试数据中的年份，停止循环
                        while(flagYear){
                            int displayYear=Integer.parseInt(calendarContraler.get(y).findElement(By.xpath("div/div/div/span[1]")).getText().split(" ")[0]);
                            int targleYear=Integer.parseInt(testDataList.get(i).split("-")[0]);
                            if(displayYear==targleYear){
                                flagYear=false;
                            }else if(displayYear>targleYear){
                                calendarContraler.get(y).findElement(By.xpath("div/div/div/button[1]")).click();
                            }else{
                                calendarContraler.get(y).findElement(By.xpath("div/div/div/button[3]")).click();
                            }
                        }
                        boolean flagMonth=true;//控制调节月按钮，当页面出现测试数据中的月份，停止循环
                        while(flagMonth){
                            int displayMonth=Integer.parseInt(calendarContraler.get(y).findElement(By.xpath("div/div/div/span[2]")).getText().split(" ")[0]);
                            int targleMonth=Integer.parseInt(testDataList.get(i).split("-")[1]);
                            if(displayMonth==targleMonth){
                                flagMonth=false;
                            }else if(displayMonth>targleMonth){
                                calendarContraler.get(y).findElement(By.xpath("div/div/div/button[2]")).click();
                            }else{
                                calendarContraler.get(y).findElement(By.xpath("div/div/div/button[4]")).click();
                            }
                        };
                        //在display的时间控件的html中，点击与测试数据中相同的“日”
                        int targleDay=Integer.parseInt(testDataList.get(i).split("-")[2]);
                        WebElement today = null;
                        String targleDayStr=targleDay+"";
                        List<WebElement> listDay=calendarContraler.get(y).findElements(By.xpath("div/div/div[2]/table/tbody/tr[@class='el-date-table__row']/td[@class='available'" +
                                "or @class='available current' " +
                                "or @class='available today current'] "));
                        for(int h=0;h<listDay.size();h++){
                            String day=listDay.get(h).getText();
//                            System.out.println(day);
                            if(listDay.get(h).getText().equals("今天")){
//                                System.out.println("找到了今天");
                                today=listDay.get(h);
//                                System.out.println(today);
                            }
                            if(targleDayStr.equals(day)){
//                                System.out.println("选择日期非今天");
                                listDay.get(h).click();//选择非今天的日期
                                break;
                            }
                            if(h==listDay.size()-1){
//                                System.out.println("选择日期今天");
                                today.click();//选择今天的日期
                            }
                        }
                    }
                }
                System.out.println("时间控件："+htmlList.get(i).split("=>")[2]+"已经点选");
            }
            //下面分支内容作用：给指定的时间控件选择日期 -time时分
            // 操作行：xpaths=>//input[@placeholder='选择日期时间']=>报名截止时间=>calendarAndTime
            //“报名截止时间”表示该时间对应的text
            // 数据行：2015-1-3-18:8

            else if(operatKey.equals("calendarAndTime")){
                Thread.sleep(1000);
                List<WebElement> elementList= getElements(htmlList.get(i));//点击元素，可出现时间控件的元素list
                for(int k=0;k<elementList.size();k++){
                    String text=elementList.get(k).findElement(By.xpath("../../../span")).getText();
                    if(text.equals(htmlList.get(i).split("=>")[2])){
                        elementList.get(k).click();//点击日期控件，调出选择框
                        Thread.sleep(1000);
                        break;
                    }
                };
                List<WebElement> calendarContraler=getElements("xpaths=>//div[@class='el-picker-panel el-date-picker has-time']");//时间控件的html结构的list，只有一个是display的
                for (int y=0;y<calendarContraler.size();y++){//遍历到display的html结构
//                    System.out.println("calendarContraler size"+calendarContraler.size());
//                    System.out.println(calendarContraler.get(y).isDisplayed());
                    if(calendarContraler.get(y).isDisplayed()){
                        boolean flagYear=true;//控制调节年按钮，当页面出现测试数据中的年份，停止循环
                        while(flagYear){
                            int displayYear=Integer.parseInt(calendarContraler.get( y).findElement(By.xpath("div[1]/div[1]/div[2]/span[1]")).getText().split(" ")[0]);
                            int targleYear=Integer.parseInt(testDataList.get(i).split("-")[0]);
                            if(displayYear==targleYear){
                                flagYear=false;
                            }else if(displayYear>targleYear){
                                calendarContraler.get(y).findElement(By.xpath("div[1]/div[1]/div[2]/button[1]")).click();
                            }else{
                                calendarContraler.get(y).findElement(By.xpath("div[1]/div[1]/div[2]/button[3]")).click();
                            }
                        }
                        boolean flagMonth=true;//控制调节月按钮，当页面出现测试数据中的月份，停止循环
                        while(flagMonth){
                            int displayMonth=Integer.parseInt(calendarContraler.get(y).findElement(By.xpath("div[1]/div[1]/div[2]/span[2]")).getText().split(" ")[0]);
                            int targleMonth=Integer.parseInt(testDataList.get(i).split("-")[1]);
                            if(displayMonth==targleMonth){
                                flagMonth=false;
                            }else if(displayMonth>targleMonth){
                                calendarContraler.get(y).findElement(By.xpath("div[1]/div[1]/div[2]/button[2]")).click();
                            }else{
                                calendarContraler.get(y).findElement(By.xpath("div[1]/div[1]/div[2]/button[4]")).click();
                            }
                        }
                        //在display的时间控件的html中，点击与测试数据中相同的日
                        int targleDay=Integer.parseInt(testDataList.get(i).split("-")[2]);
                        WebElement today = null;
                        String targleDayStr=targleDay+"";
                        List<WebElement> listDay=calendarContraler.get(y).findElements(By.xpath("div[1]/div[1]/div[3]/table/tbody/tr[@class='el-date-table__row']/td[@class='available' or @class='available today current' or @class='available current']"));
//                        System.out.println(listDay.size());
                        for(int h=0;h<listDay.size();h++){
                            String day=listDay.get(h).getText();
//                            System.out.println(day);
                            if(listDay.get(h).getText().equals("今天")){
//                                System.out.println("找到了今天");

                                today=listDay.get(h);
//                                System.out.println(today);
                            }
                            if(targleDayStr.equals(day)){
//                                System.out.println("选择日期非今天");
                                listDay.get(h).click();//选择非今天的日期
                                break;
                            }
                            if(h==listDay.size()-1){
//                                System.out.println("选择日期今天");
                                today.click();//选择今天的日期
                            }
                        }
                        //选择 某时
                        List<WebElement> hourAndSecControllerList=getElements("xpaths=>//input[@placeholder='选择时间']");
                        for (int t=0;t<hourAndSecControllerList.size();t++){
                            if(hourAndSecControllerList.get(t).isDisplayed()){
                                hourAndSecControllerList.get(t).click();
                                System.out.println("激活时分秒控件");
                                Thread.sleep(1500);
                            }
                        }
                        List<WebElement> hourTextList=calendarContraler.get(y).findElements(By.xpath("div/div/div[1]/span[2]/div[2]/div[1]/div[1]/div[1]/div[1]/ul/li[@class='el-time-spinner__item' or @class='el-time-spinner__item active']"));
//                        System.out.println("hourTextListsize:"+hourTextList.size());
                        WebElement hourTextIsNull= null;
                        for(int u=0;u<hourTextList.size();u++){
                            String targleHour=testDataList.get(i).split("-")[3].split(":")[0];
                            String hourInhtml=hourTextList.get(u).getText();
//                            System.out.println(hourInhtml);
                            if(hourInhtml.equals("")){//说明：在hourTextList，由于前端原因，会有一个元素不可见，获取的text是null，此条件分支是处理这个情况
                                new Actions(driver).moveToElement(hourTextList.get(u)).build().perform();
                                hourTextIsNull=hourTextList.get(u);
                                new Actions(driver).moveToElement(hourTextList.get(0)).build().perform();
                            }
                            if(targleHour.equals(hourInhtml)){
//                                new Actions(driver).moveToElement(hourTextList.get(u)).build().perform();
                                hourTextList.get(u).click();//点击时
//                                System.out.println("选的时间是可见的，已点击");
                                break;
                            }
                            if(u==hourTextList.size()-1){
                                hourTextIsNull.click();//点击时
//                                System.out.println("选的时间是不可见的，已点击");
                            }
                        }
                        //选择 某分
                        List<WebElement> minTextList=calendarContraler.get(y).findElements(By.xpath("div[1]/div[1]/div[1]/span[2]/div[2]/div[1]/div[1]/div[2]/div[1]/ul/li[@class='el-time-spinner__item' or @class='el-time-spinner__item active']"));
//                        System.out.println("minTextList size:"+minTextList.size());
                        WebElement minTextIsNull=null;
                        for(int f=0;f<minTextList.size();f++){
                            String targleMin=testDataList.get(i).split("-")[3].split(":")[1];
                            String minInHtml=minTextList.get(f).getText();
//                            System.out.println(minInHtml);
                            if(minInHtml.equals("")){
                                new Actions(driver).moveToElement(minTextList.get(f)).build().perform();
                                minTextIsNull=minTextList.get(f);
                                new Actions(driver).moveToElement(minTextList.get(0)).build().perform();
                            }
                            if (targleMin.equals(minInHtml)){
                                minTextList.get(f).click();
                                break;
                            }
                            if(f==minTextList.size()-1){
                                minTextIsNull.click();
                            }
                        }
                        //点击确定
                        calendarContraler.get(y).findElement(By.xpath("div[1]/div[1]/div[1]/span[2]/div[2]/div[2]/button[2]")).click();
                        calendarContraler.get(y).findElement(By.xpath("div[2]/button")).click();

                        break;
                    }
                }
            }
            //下面分支内容作用：给指定的下拉框选择相应的选项
            // 操作行：xpaths=>//input[@placeholder='请选择']=>用户类型=>select
            //“用户类型”表示选择框的text名称， //input[@placeholder='请选择']表示激活下拉框的路径
            // 数据行：填入要选择的option的text
            //说明：需要先定位到下拉框的的html元素，加入到list，遍历到可见元素，并且与excel 中数据匹配，从而确定具体的optionValue
            else if(operatKey.equals("select")){
                List<WebElement> selectButtonList=getElements(htmlList.get(i));
                for(int t=0;t<selectButtonList.size();t++){
                    String selecetButtonNameInHtml=selectButtonList.get(t).findElement(By.xpath("../../../../span[1]")).getText();
                    String selecetButtonNameInOperationRow=htmlList.get(i).split("=>")[2];
//                    System.out.println("页面取出来的:"+selecetButtonNameInHtml);
//                    System.out.println("excel读出来的:"+selecetButtonNameInOperationRow);
                    if(selecetButtonNameInHtml.equals(selecetButtonNameInOperationRow)){
                        selectButtonList.get(t).click();//点击可选框，触发下拉框
                        System.out.println("触发下拉框: "+selecetButtonNameInHtml);
                        Thread.sleep(500);//不可删除，等待后续页面发生变化
                        break;
                    }

                }

                List<WebElement> selectDivList=getElements("xpaths=>//div[@class='el-select-dropdown el-popper']");//该list的每个元素，包含每个选项框中的可选项
                for(int u=0;u<selectDivList.size();u++){
                    boolean isdisplay=selectDivList.get(u).isDisplayed();
                    System.out.println("共"+selectDivList.size()+"个元素，第"+u+"个元素是否可见："+isdisplay);
                    if(isdisplay){
                        List<WebElement> optionSpanList=selectDivList.get(u).findElements(By.xpath("div/div/ul/li/span"));
                        System.out.println("可选项size"+optionSpanList.size());
                        for(int e=0;e<optionSpanList.size();e++){
                            String optionValueInHtml=optionSpanList.get(e).getText();
                            String optionValueInTestData=testDataList.get(i);
                            System.out.println("页面中的option："+optionValueInHtml);
                            System.out.println("excel中的option："+optionValueInTestData);
                            if (optionValueInHtml.equals(optionValueInTestData)){
                                //以下执行鼠标动作的方法作用：解决选项值需要下拉滚动条时，才能点击到的问题
                                //解决思路：直接移动到最后一个选项值，再点击要选择的option，就能点击了
                                new Actions(driver).moveToElement(optionSpanList.get(optionSpanList.size()-1)).perform();
                                optionSpanList.get(e).click();//点击选项值
                                System.out.println("选择下拉框中的："+optionValueInHtml);
                                Thread.sleep(500);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            //设置页面展示数据最大条数下拉框
            //操作行xpath=>//span[@class='el-pagination__sizes']=>setMaxDatasDisplay
            //数据行:40 条/页
            else if(operatKey.equals("setMaxDatasDisplay")){
                getElement(htmlList.get(i)).click();//点击每页展示条数的下拉框
                Thread.sleep(1000);
                List<WebElement> list=getElements("xpaths=>//ul[@class='el-scrollbar__view el-select-dropdown__list']/li/span");
                boolean isClick=false;
//                System.out.println(list.size());
                for(int t=0;t<list.size();t++){
                    String optionValueInHtml=list.get(t).getText();
                    String optionValueInTestData=testDataList.get(i);
//                    System.out.println("页面取值："+optionValueInHtml+"  excel取值："+optionValueInTestData);
                    if(optionValueInHtml.equals(optionValueInTestData)){
                        list.get(t).click();
                        System.out.println("选择显示："+optionValueInTestData);
                        isClick=true;
                        break;
                    }
                }
                if(!isClick){
                    System.out.println("没有点击到要显示的条数");
                }
                Thread.sleep(1000);
            }
            //翻页-向右翻一页
            //操作行：pageDown
            //数据行：boolean
            else if(operatKey.equals("pageDown")&&testDataList.get(i).toLowerCase().equals("true")){
                Thread.sleep(1000);
                getElement("xpath=>//div[@class='el-pagination']/button[1]").click();
                System.out.println("向右翻一页");
            }
            //翻页-向左翻一页
            //操作行：pageUp
            //数据行：boolean
            else if(operatKey.equals("pageUp")&&testDataList.get(i).toLowerCase().equals("true")){
                Thread.sleep(1000);
                getElement("xpath=>//div[@class='el-pagination']/button[2]").click();
                System.out.println("向左翻一页");
            }
            //翻页-翻到指定页面
            //操作行：pageTurnTo
            //数据行：boolean
            else if(operatKey.equals("pageTurnTo")){
                Thread.sleep(1000);
                List<WebElement> pageNumList=getElements("xpaths=>//div[@class='el-pagination']/ul/li");
                for(int r=0;r<pageNumList.size();r++){
                    String pageNumInHtml=pageNumList.get(r).getText();
                    String pageNumInExcel=testDataList.get(i);
                    if(pageNumInExcel.equals(pageNumInHtml)){
                        pageNumList.get(r).click();
                        System.out.println("选择了第："+pageNumInExcel+"页");
                        break;
                    }
                }
            }
            //向右翻多页，点击向右翻页的按钮“...”
            //操作行：turnDownMore
            //数据行：boolean
            else if(operatKey.equals("turnDownMore")&&testDataList.get(i).toLowerCase().equals("true")){
                Thread.sleep(1000);
                List<WebElement> pageNumList=getElements("xpaths=>//div[@class='el-pagination']/ul/li");
                boolean hasButtonClass=false;
                for(int e=0;e<pageNumList.size();e++){
                    String buttonClass=pageNumList.get(e).getAttribute("class");
//                    System.out.println(buttonClass);
                    if(buttonClass.equals("el-icon more btn-quicknext el-icon-more")){
                        pageNumList.get(e).click();
                        System.out.println("点击了向右翻多个页面按钮");
                        hasButtonClass=true;
                        break;
                    }
                }
                if(!hasButtonClass){
                    System.out.println("注意：未出现向右翻多个页面的按钮");
                }
            }
            //向右翻多页，点击向左翻页的按钮“...”
            //操作行：turnUpMore
            //数据行：boolean
            else if(operatKey.equals("turnUpMore")&&testDataList.get(i).toLowerCase().equals("true")){
                Thread.sleep(1000);
                List<WebElement> pageNumList=getElements("xpaths=>//div[@class='el-pagination']/ul/li");
                boolean hasButtonClass=false;
                for(int e=0;e<pageNumList.size();e++){
                    String buttonClass=pageNumList.get(e).getAttribute("class");
//                    System.out.println(buttonClass);
                    if(buttonClass.equals("el-icon more btn-quickprev el-icon-more")){
                        pageNumList.get(e).click();
                        System.out.println("点击了向左翻多个页面按钮");
                        hasButtonClass=true;
                        break;
                    }
                }
                if(!hasButtonClass){
                    System.out.println("注意：未出现向左翻多个页面的按钮");
                }
            }


            //下面分支内容作用：点击单选按钮，radioGroup
            // 操作行：xpaths=>//div[@class='el-radio-group']=>../../span=>是否城市清单=>radioGroup
            //“是否城市清单”表示要定位的radiGroup的名字， //div[@class='el-radio-group']表示页面所有的radioGroup的list
            //“../../span”表示该radioGroup的名字
            //“radioGroup”标志作用
            // 数据行：选择的目标名称
            //说明：遍历到目标radioGroup，在目标radioGroup中遍历到具体的radioButton，点击选择
            else if(operatKey.equals("radioGroup")){
                List<WebElement> radioGroupList=getElements(htmlList.get(i));
                for(int l=0;l<radioGroupList.size();l++){
                    String radioGroupNameInHtml=radioGroupList.get(l).findElement(By.xpath(htmlList.get(i).split("=>")[2])).getText();
//                    System.out.println("radioGroup size():"+radioGroupList.size());
//                    System.out.println("遍历到radioGroupNameInHtml:"+radioGroupNameInHtml);
                    String radioGroupNameInOperatRow=htmlList.get(i).split("=>")[3];
                    if(radioGroupNameInHtml.equals(radioGroupNameInOperatRow)){
                        List<WebElement> radioNamesList=radioGroupList.get(l).findElements(By.xpath("label/span[2]"));
//                        System.out.println("radioName Size"+radioNamesList.size());
                        for(int e=0;e<radioNamesList.size();e++){
                            String yourChoiceInTestData=testDataList.get(i);
                            String radioName=radioNamesList.get(e).getText();
//                            System.out.println("遍历到radioName："+radioName);
                            if(yourChoiceInTestData.equals(radioName)){
                                radioNamesList.get(e).click();
                                System.out.println("点击了选项："+radioName);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            //获取员工编号，使用场景：在新增企业员工时使用该方法，获取员工编号，为后续步骤断言员工编号来判断是否新增成功做准备
            //操作行：getEmployeeNum
            //数据行：boolean
            else if(operatKey.equals("getEmployeeNum")&&testDataList.get(i).toLowerCase().equals("true")){
                //执行js获取员工编号
                Thread.sleep(1500);
                String js = "return document.getElementsByClassName('el-input is-disabled')[0].getElementsByTagName('input')[0].value";
                ((JavascriptExecutor)driver).executeScript(js);
                employeeNumInhtml=(String) ((JavascriptExecutor)driver).executeScript(js);
                System.out.println("页面获取的员工编号（局部变量）:"+employeeNumInhtml);
            }

            //断言员工编号和员工信息字段是否对应
            //操作行：E2017001=>员工名称=>assertEmployeeInfo
            //数据行：张三
            //说明：断言员工编号：E2017001是否对应员工姓名：张三
            //当用例中有新增员工步骤，且有步骤“getEmployeeNum”时，员工编号自动获取为局部变量，再调用本分支时，不会取到E2017001
            else if(operatKey.equals("assertEmployeeInfo")){
                Thread.sleep(2000);

                //定位thead中的各列头名称
                List<WebElement> headerList =getElements("xpaths=>//div[@class='el-table el-table--fit el-table--border el-table--" +
                        "enable-row-transition']//div[@class='el-table__header-wrapper']/table//th/div");
                //找到员工编号列
                boolean employeeNumHasTitle=false;
                int f;
                for(f=0;f<headerList.size();f++){
                    String headerName=headerList.get(f).getText();
                    if(headerName.equals("员工编号")){
                        employeeNumHasTitle=true;
//                        System.out.println("员工编号的index："+f);
                        break;
                    }
                }
                //找到员工信息列
                boolean employeeInfoHasTitle=false;
                String employeeInfo=null;
                if(employeeNumInhtml==null){
                     employeeInfo=htmlList.get(i).split("=>")[0];
                }else{
                    employeeInfo=htmlList.get(i).split("=>")[1];
                }

                int r;
                for(r=0;r<headerList.size();r++){
                    String headerName=headerList.get(r).getText();
                    if(headerName.equals(employeeInfo)){
                        employeeInfoHasTitle=true;
//                        System.out.println("employeeInfo的index："+r);
                        break;
                    }
                }
                if(!employeeInfoHasTitle|| !employeeNumHasTitle){
                    System.out.println("注意：表头中没有找到‘员工编号’列或‘"+employeeInfo+"’列");
                }else{
                    int t=f+1;//“员工编号”在<tr>中的第几个<td>,为xpath定位到“员工编号”<td>时使用
                    int s=r+1;//“员工信息”在<tr>中的第几个<td>,为xpath定位到“员工信息”<td>时使用
//                    System.out.println("得到的员工编号<td>index t:"+t);
//                    System.out.println("得到的员工信息<td>index s:"+s);

                    List<WebElement> employeeNumList=getElements(
                            "xpaths=>//div[@class='el-table el-table--fit el-table--border el-table--enable-row-transition']/" +
                                    "div[@class='el-table__body-wrapper is-scrolling-none']//tr/td["+t+"]/div");

                    //获取预期员工编号，如果前面有新增员工步骤，员工编号从本函数局部变量中获取
                    //如果没有新增步骤，员工编号从excel中获取
                    if(employeeNumInhtml==null){
                        employeeNumInhtml=htmlList.get(i).split("=>")[0];
                        System.out.println("预期输出的\"员工编号\"从excel中获取："+employeeNumInhtml);
                    }
//                  System.out.println("预期输出的\"员工编号\"在新增企业员工试获取："+employeeNumInhtml);
                    boolean hasTheSameEmployeeNum=false;
                    //遍历页面存在的员工编号，当和预期的一致时，断言该“员工编号”对应的“员工信息”是否和excel中提供的“员工信息”一致
                    for(int c=0;c<employeeNumList.size();c++){
                        String actualEmployeeNum=employeeNumList.get(c).getText();//从页面获取实际的员工编号
//                        System.out.println("页面存在的员工编号："+actualEmployeeNum);
                        if(actualEmployeeNum.equals(employeeNumInhtml)){//如果从页面获取的员工编号和预期员工编号相等，则判断该员工编号的员工信息是否和excel中的一致
                            String employeeInfoForActualResult=employeeNumList.get(c).findElement(By.xpath("../../td["+s+"]/div")).getText();
                            String employeeInfoInExel=testDataList.get(i);
                            if(failStop){
                                assertEquals(employeeInfoForActualResult,employeeInfoInExel);
                            }else{
                                try {
                                    assertEquals(employeeInfoForActualResult,employeeInfoInExel);
                                    System.out.println("断言成功");
                                }catch (Error e){
                                    asserFailCase++;
                                    passedCase--;
                                    testCaseResultList.add("断言失败");
                                    testCaseResultList.add("员工编号："+employeeNumInhtml+" 存在，但是对应的‘"+employeeInfo+"’不是："+employeeInfoInExel+"<br>"+e.getMessage());//用例报错信息加入到testCaseResultList
                                    System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                                    Thread.sleep(200);
                                    e.printStackTrace();
                                    Thread.sleep(200);
                                }
                            }
                            hasTheSameEmployeeNum=true;
                            break;
                        }
                    }
                    //当页面不存在预期的员工编号时，抛异常
                    if(!hasTheSameEmployeeNum){
                        System.out.println("注意：页面没有出现员工编号："+employeeNumInhtml);
                        if(failStop){
                            assertTrue(hasTheSameEmployeeNum);
                        }else{
                            try {
                                assertTrue(hasTheSameEmployeeNum);
                            }catch (Error e){
                                asserFailCase++;
                                passedCase--;
                                testCaseResultList.add("断言失败");
                                testCaseResultList.add("注意：页面没有出现员工编号："+employeeNumInhtml+"<br>"+e.getMessage());//用例报错信息加入到testCaseResultList
                                System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                                Thread.sleep(200);
                                e.printStackTrace();
                                Thread.sleep(200);
                            }
                        }
                    }
                }
            }
            //断言htlm元素的属性值
            // 操作行：id=>Weba=>class=>assertHtmlAttribute，表示获取target的属性值
            // 数据行：预期输出
            else if(operatKey.equals("assertHtmlAttribute")){
                String actualResult=getElement(htmlList.get(i)).getAttribute(htmlList.get(i).split("=>")[2]);
                String expectResult=testDataList.get(i);
                if(failStop){
                    assertEquals(actualResult,expectResult);
                }else{
                    try {
                        assertEquals(actualResult,expectResult);
                    }catch (Error e){
                        asserFailCase++;
                        passedCase--;
                        testCaseResultList.add("断言失败");
                        testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
                        System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                        Thread.sleep(200);
                        e.printStackTrace();
                        Thread.sleep(200);
                    }
                }
            }

            //断言title
            // 操作行：预期输出=>title
            // 数据行：预期输出
            else  if(operatKey.equals("assertTitle")){
                String actualResult=driver.getTitle();
                String expectResult=testDataList.get(i);
                //根据Boolean failStop的值，来判断执行断言时失败是，是否终止执行，true终止执行，false继续执行
                if(failStop){
                    assertEquals(actualResult,expectResult);
                }else{
                    try {
                        assertEquals(actualResult,expectResult);
                    }catch (Error e){
                        asserFailCase++;
                        passedCase--;
                        testCaseResultList.add("断言失败");
                        testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
                        System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                        Thread.sleep(200);
                        e.printStackTrace();
                        Thread.sleep(200);
                    }
                }
            }


            //断言alert的内容并单击确认
            // 操作行：预期输出=>alert
            // 数据行：预期输出
            else if(htmlList.get(i).contains("预期输出=>alert")){//断言弹框-alert（关闭alert，并取出弹框内容进行断言）
                String actualResult=driver.switchTo().alert().getText();
                String expectResult=testDataList.get(i);
                driver.switchTo().alert().accept();
                //根据Boolean failStop的值，来判断执行断言时失败是，是否终止执行，true终止执行，false继续执行
                if(failStop){
                    assertEquals(actualResult,expectResult);
                }else{
                    try {
                        assertEquals(actualResult,expectResult);
                    }catch (Error e){
                        asserFailCase++;
                        passedCase--;
                        testCaseResultList.add("断言失败");
                        testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
                        System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                        Thread.sleep(200);
                        e.printStackTrace();
                        Thread.sleep(200);
                    }
                }
            }
            //断言html标签对之间的内容。
            // 操作行：id=>3=>assertText
            // 数据行：预期输出
            else if (operatKey.equals("assertText")){
                String actualResult=getElement(htmlList.get(i)).getText();
                String expectResult=testDataList.get(i);
                //根据Boolean failStop的值，来判断执行断言时失败是，是否终止执行，true终止执行，false继续执行
                if(failStop){
                    assertEquals(actualResult,expectResult);
                }else{
                    try {
                        assertEquals(actualResult,expectResult);
                        System.out.println("断言成功");
                    }catch (Error e){
                        asserFailCase++;
                        passedCase--;
                        System.out.println("断言失败");
                        testCaseResultList.add("断言失败");
                        testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
                        System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                        Thread.sleep(200);
                        e.printStackTrace();
                        Thread.sleep(200);
                    }
                }
            }
            //专门断言input元素的value，(原因：在某些查看信息页面，input框中的值没显示在html标签对之间)
            //操作行：员工名称=>assertInputValue
            //数据行：预期输出
            //说明：“员工名称”表示要断言的input在页面显示的名称
            else if(operatKey.equals("assertInputValue")){
                Thread.sleep(1500);
                String inputName=htmlList.get(i).split("=>")[0];
                String js=null;
                //一下两种分支中的js，if针对断言“用户账号”的input，不通用；else 针对各种input通用
                if(inputName.equals("用户账号")){
                    js="var list=document.getElementsByClassName('fullline');\n" +
                            "\tconsole.log(list.length);\n" +
                            "\tfor(var i=0;i<list.length;i++){\n" +
                            "\t\tconsole.log(\"i取值：\"+i);\n" +
                            "\t\tvar spanlist=list[i].getElementsByTagName('span');\n" +
                            "\t\tfor(var p=0;p<spanlist.length;p++){\n" +
                            "\t\t\tvar titleName=spanlist[p].textContent\n" +
                            "\t\t\tconsole.log(\"titleName\"+titleName);\n" +
                            "\t\t\tif(titleName===\"用户帐号\"){\n" +
                            "\t\t\t\treturn list[i].getElementsByTagName('input')[p].value;\n" +
                            "\n" +
                            "\t\t\t}\n" +
                            "\n" +
                            "\t\t}\n" +
                            "\t}";

                }else{
                    js="var list=document.getElementsByClassName('fullline');\n" +
                            "\tconsole.log(list.length);\n" +
                            "\tfor(var i=0;i<list.length;i++){\n" +
                            "\t\tconsole.log(\"i取值：\"+i);\n" +
                            "\t\tvar spanlist=list[i].getElementsByTagName('span');\n" +
                            "\t\tfor(var p=0;p<spanlist.length;p++){\n" +
                            "\t\t\tvar titleName=spanlist[p].textContent\n" +
                            "\t\t\tconsole.log(\"titleName\"+titleName);\n" +
                            "\t\t\tif(titleName===\""+inputName+"\"){\n" +
                            "\t\t\t\tconsole.log(\"进入！！！！！！！！！！！！！\");return list[i].getElementsByTagName('input')[p].value;\n" +
                            "\t\t\t}\n" +
                            "\t\t}\n" +
                            "\t}";

                }





//                ((JavascriptExecutor)driver).executeScript(js);//此行不要注释
                String actualResult=(String) ((JavascriptExecutor)driver).executeScript(js);
                String expectResult=testDataList.get(i);
                System.out.println("执行js获取的input的Value："+actualResult);
                //根据Boolean failStop的值，来判断执行断言时失败是，是否终止执行，true终止执行，false继续执行
                if(failStop){
                    assertEquals(actualResult,expectResult);
                }else{
                    try {
                        assertEquals(actualResult,expectResult);
                        System.out.println("断言成功");
                    }catch (Error e){
                        asserFailCase++;
                        passedCase--;
                        System.out.println("断言失败");
                        testCaseResultList.add("断言失败");
                        testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
                        System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                        Thread.sleep(200);
                        e.printStackTrace();
                        Thread.sleep(200);
                    }
                }
            }
            //断言元素是否存在
            //操作行xpath=>//div[@class='el-table__body-wrapper']//a[text()='wujj']=>assertElementIsDisplay
            //数据行 boolean(true存在 ；false不存在)
            else if(operatKey.equals("assertElementIsDisplay")){
                boolean expectResult=Boolean.parseBoolean(testDataList.get(i).toLowerCase());
                boolean actualResult=getElement(htmlList.get(i)).isDisplayed();

                if(failStop){
                    assertEquals(actualResult,expectResult);
                }else{
                    try {
                        assertEquals(actualResult,expectResult);
                        System.out.println("断言成功");
                    }catch (Error e){
                        asserFailCase++;
                        passedCase--;
                        System.out.println("断言失败");
                        testCaseResultList.add("断言失败");
                        testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
                        System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                        Thread.sleep(200);
                        e.printStackTrace();
                        Thread.sleep(200);
                    }
                }
            }

            //断言页面存在数据条数，适用于选择页面最大显示条数后使用
            //操作行：assertDisplayDatasNum
            //数据行：10
            else if(operatKey.equals("assertDisplayDatasNum")){
                int actualResult=getElements("xpaths=>//div[@class='el-table__body-wrapper']//tbody/tr").size();
                int expectResult=Integer.parseInt(testDataList.get(i));
                if(failStop){
                    assertEquals(actualResult,expectResult);
                }else{
                    try {
                        assertEquals(actualResult,expectResult);
                        System.out.println("断言成功");
                    }catch (Error e){
                        asserFailCase++;
                        passedCase--;
                        System.out.println("断言失败");
                        testCaseResultList.add("断言失败");
                        testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
                        System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
                        Thread.sleep(200);
                        e.printStackTrace();
                        Thread.sleep(200);
                    }
                }
            }
            //
            //  else{
//                try{
//                    Assert.fail("在给出的操作行的数据中："+htmlList.get(i)+"没有找到对应的操作方法，请注意格式");
//                }catch (Error e){
//                    asserFailCase++;
//                    passedCase--;
//                    testCaseResultList.add("断言失败");
//                    testCaseResultList.add(e.getMessage());//用例报错信息加入到testCaseResultList
//                    System.out.println("执行用例："+testDataList.get(0)+" 断言失败，失败信息如下：");
//                    Thread.sleep(200);
//                    e.printStackTrace();
//                    Thread.sleep(200);
//                }
//            }
        }
    }
    public void formSubmits(String startAndEndTag) throws InterruptedException, IOException {
        /**
         * @Author:SH
         * @Date:10:55 2017/12/15
         * @Description:执行在_start和_end之间的用例
         * excel有两行第一个单元格分别标记了“业务1_statr”“业务1_end”,
         * 那么startAndEndTag=业务1，表示执行 “业务1_statr”“业务1_end”这两行之间的用例
         * @params  * @param startAndEndTag _start和_end之前的名称
         */
        for(int i=((readExcel.firstCellOfRowsList.indexOf(startAndEndTag+"_star`t"))+1);i<readExcel.firstCellOfRowsList.indexOf(startAndEndTag+"_end");i++){
            formSubmit(sheetMap.get(startAndEndTag+"_start"),sheetMap.get(readExcel.firstCellOfRowsList.get(i)));
        }
    }

    public void goToWindow(String num){
        /**
         * @Author:SH
         * @Date:9:22 2017/12/21
         * @Description:切换页面
         * @params  * @param num  切换到第几个打开的窗口，excel中的表示方法“换到新窗口=>3”
         */
        String nowHandle=driver.getWindowHandle();//获取当前所在窗口句柄
        Set<String> nowAllHandles=driver.getWindowHandles();//获取所有窗口句柄

        if(allHandlesList.contains(nowHandle)){
//            System.out.println(nowHandle.toString()+"  已经包含在"+allHandlesList.toString());
        }else{
//            System.out.println(nowHandle.toString()+" 不在 "+allHandlesList.toString());
            allHandlesList.add(nowHandle);
//            System.out.println("由于不在，所以添加到后："+allHandlesList.toString());
        }
//        System.out.println("现在已经打开的窗口"+nowAllHandles.toString());
        for(String handle:nowAllHandles){
            if(handle.equals(nowHandle)||(allHandlesList.contains(handle))){
            }else{
                allHandlesList.add(handle);
            }
        }
//        System.out.println("我的allHandlesList"+allHandlesList.toString());
        driver.switchTo().window(allHandlesList.get(Integer.parseInt(num)-1));
//        System.out.println("现在切换到"+allHandlesList.get(Integer.parseInt(num)-1)+"url："+driver.getCurrentUrl());
    }
    //公共方法:定位
    public WebElement getElement(String location){
        /**
         * @Author:SH
         * @Date:13:59 2017/12/15
         * @Description:根据接受到的参数，选择不同的定位方法去定位
         * @params  * @param location “id=>1”表示id，根据“id=1”这种方式去定位
         */


        if(!location.contains("=>")){
            Assert.fail("您给出的定位方式缺少:"+"=>");
        }
        String by = location.split("=>")[0];
        String value = location.split("=>")[1];
        By findelement = null;
        if (by.equals("id")) {
            findelement = By.id(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            WebElement element = driver.findElement(By.id(value));
            return element;
        } else if (by.equals("name")) {
            findelement = By.name(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            WebElement element = driver.findElement(By.name(value));
            return element;
        } else if (by.equals("class")) {
            findelement = By.className(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            WebElement element = driver.findElement(By.className(value));
            return element;
        } else if (by.equals("linkText")) {
            findelement = By.linkText(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            WebElement element = driver.findElement(By.linkText(value));
            return element;
        } else if (by.equals("xpath")) {
            findelement = By.xpath(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            WebElement element = driver.findElement(By.xpath(value));
            return element;
        } else if (by.equals("css")) {
            findelement = By.cssSelector(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            WebElement element = driver.findElement(By.cssSelector(value));
            return element;
        } else {
            System.out.println("请维护excel表中的定位方式");
            Assert.fail("Please enter the correct targeting elements,'id','name','class','xpath','css'.");
        }
        return null;
    }


    //公共方法:定位
    public List<WebElement> getElements(String location){
        /**
         * @Author:SH
         * @Date:13:59 2017/12/15
         * @Description:根据接受到的参数，选择不同的定位方法去定位,获得找到的元素列表
         * @params  * @param location “ids=>1”表示定位所有id=1的元素
         */

        if(!location.contains("=>")){
            Assert.fail("您给出的定位方式缺少:"+"=>");
        }
        String by = location.split("=>")[0];
        String value = location.split("=>")[1];
        By findelement = null;
        if (by.equals("ids")) {
            findelement = By.id(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            List<WebElement> elements = driver.findElements(By.id(value));
            return elements;
        } else if (by.equals("names")) {
            List<WebElement> elements = driver.findElements(By.name(value));
            return elements;
        } else if (by.equals("classes")) {
            List<WebElement> elements = driver.findElements(By.className(value));
            return elements;
        } else if (by.equals("linkTexts")) {
            List<WebElement> elements = driver.findElements(By.linkText(value));
            return elements;
        } else if (by.equals("xpaths")) {
            findelement = By.xpath(value);
            new WebDriverWait(driver,watiElementSec).until(ExpectedConditions.presenceOfElementLocated(findelement));
            List<WebElement> elements = driver.findElements(By.xpath(value));
            return elements;
        } else if (by.equals("csses")) {
            List<WebElement> elements = driver.findElements(By.cssSelector(value));
            return elements;
        } else {
            System.out.println("请维护excel表中的定位方式");
            Assert.fail("Please enter the correct targeting elements,'ids','names','classes','xpahts','csses'.");
        }
        return null;
    }
//    public static  void main(String[] args) throws IOException, InterruptedException {
//        TestDemo testDemo=new TestDemo();
//        testDemo.testTest();
//    }
}
