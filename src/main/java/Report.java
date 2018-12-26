import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class Report {
    PrintStream printStream;

    public void creatReport(List<List<String>> reportInfo,int totalCase,int passedCase,
                            int errorCase,int asserFailCase,String reportPath,String startTime,String endTime,
                            boolean reportHtmlAddStamp){
        /**
         * @Author:SH
         * @Date:10:28 2017/12/15
         * @Description:该方法生成html格式化的测试报告
         * @params  * @param reportInfo 测试类的测试结果
         * @param totalCase 测试用例数量
         * @param passedCase 通过的测试用例数量
         * @param errorCase 定位失败的测试用例数量
         * @param asserFailCase 断言失败的测试用例数量
         */
        System.out.println("》》》》》》》》》》》》》》》》》开始生成测试报告《《《《《《《《《《《《《《《《《");
//        System.out.println("接收到的reportInfo：》》》》》》》》》》》》》》》"+reportInfo);

        //新建listForJs，
        List<List<String>>  listForJs=new ArrayList<List<String>>();//listForJs最终作用：把该list中的内容转换成字符串，在html中为js的二维数组
        for(int i=0;i<reportInfo.size();i++){
            List<String> caseResult=new ArrayList<String>();
            for(int p=0;p<reportInfo.get(i).size();p++){
                caseResult.add("\""+reportInfo.get(i).get(p).replaceAll("\n","")+"\"");
            }
            listForJs.add(caseResult);
        }
//        System.out.println("把接收到reportInfo转换成html中js的数组字符串为"+listForJs);


        //用于存储html字符串
        StringBuilder stringHtml = new StringBuilder();


        try{
            //打开文件 如果文件存名存在，则直接修改文件，如果文件名不存在，直接生成文件
            printStream = new PrintStream(new FileOutputStream(reportPath));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        //输入HTML文件内容
        stringHtml.append("<html>" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\" />" +
                "<meta name=\"Su\">" +
                "<meta name=\"description\" content=\"自动化测试用例结果列表\">" +
                "<title>UI自动化测试报告</title>" +



                "<script type=\"text/javascript\">\n" +
                        "        var reportInfo="+listForJs.toString()+";\n" +

                        "        function displayDate(id){\n" +
                        "            // document.getElementById(id).innerHTML=mylist[3];\n" +
                        "            result=document.getElementById(id).innerHTML;\n" +
                        "            if (result===\"断言失败\"||result===\"定位失败\") {\n" +
                        "                document.getElementById(id).innerHTML=reportInfo[id][2];\n" +
                        "            }else{\n" +
                        "                document.getElementById(id).innerHTML=reportInfo[id][1];\n" +
                        "            }\n" +
                        "            \n" +
                        "        }\n" +
                        "        \n" +
                        "\n" +
                        "window.onload=function(){\n" +
                        "            for (var i = reportInfo.length - 1; i >= 0; i--) {\n" +
                        "                if(document.getElementById(i).innerHTML===\"通过\"){\n" +
                        "                    document.getElementById(i).style.color=\"Green\";\n" +
                        "                }else{\n" +
                        "                    document.getElementById(i).style.color=\"Red\";    \n" +
                        "                }\n" +
                        "                \n" +
                        "            }\n" +
                        "\n" +" var a=document.getElementById(\"errorCase\").innerHTML\n" +
                "            var b=document.getElementById(\"failCase\").innerHTML\n" +
                "            var c=document.getElementById(\"passCase\").innerHTML\n" +
                "            if(a.indexOf(\"0\")==-1){\n" +
                "                document.getElementById(\"errorCase\").style.color=\"Red\";\n" +
                "            }else{\n" +
                "                document.getElementById(\"errorCase\").style.color=\"Green\";\n" +
                "            }\n" +
                "            if(b.indexOf(\"0\")==-1){\n" +
                "                document.getElementById(\"failCase\").style.color=\"Red\";\n" +
                "            }else{\n" +
                "                document.getElementById(\"failCase\").style.color=\"Green\";\n" +
                "            }\n" +
                "\n" +
                "            document.getElementById(\"passCase\").style.color=\"Green\";"+
                        "        }"+
                        "    </script>"+




                "</head>" +
                "<body STYLE=\"background-color:#CCFFFF;\" align=\"center\">" +
//                "<br><a style=\"font-weight:bold;\">测试用例运行结果列表</a>" +
                "<br>"+
                "<h1>ui自动化测试结果</h1>"+
                "<br>" +
                "<br>" +
                "<table width=\"90%\" height=\"50\"  align=\"center\" style=\"table-layout:fixed;\">\n" +
//                        "        <tr>\n" +
//                        "            <td>测试开始时间：</td>\n" +
//                        "        </tr>\n" +
//                        "        <tr>\n" +
//                        "            <td>测试结束时间：</td>\n" +
//                        "        </tr>\n" +
                        "        \n" +"<tr>\n" +
                "            <td >定位失败用例数：<span id=\"errorCase\">"+errorCase+"</span></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td >断言失败用例数：<span id=\"failCase\">"+asserFailCase+"</span></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td >测试通过用例数：<span id=\"passCase\">"+passedCase+"</span></td>\n" +
                "        </tr>"+
                        "            <td>总共执用例数：  "+totalCase+"</td>\n" +"<tr>\n" +
                        "            <td>测试开始时间：  "+startTime+"</td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "            <td>测试结束时间：  "+endTime+"</td>\n" +
                        "        </tr>"+
                        "        </tr>\n" +
                        "                    \n" +
                        "    </table>"+
                "<table width=\"90%\" height=\"80\" border=\"1\" align=\"center\" style=\"table-layout:fixed;\" cellspacing=\"0\" > "+
                "<thead>" +
                "<tr>" +
                "<th>测试用例名</th>" +
                "<th>执行结果</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody style=\"word-wrap:break-word;font-weight:bold;text-align: center;\">");
        //从接收的测试结果reportInfo中获取实际测试结果，展示在html中
        for(int i=0;i<reportInfo.size();i++){
            stringHtml.append("<tr>");
            stringHtml.append("<td>"+reportInfo.get(i).get(0)+"</td>");
            stringHtml.append("<td id=\""+i+"\" href=\"javascript:void(0)\" onclick=\"displayDate('"+i+"')\">"+reportInfo.get(i).get(1)+"</td>");
            stringHtml.append("</tr>");
        }
        stringHtml.append("</tbody>");

        try{
            //将HTML文件内容写入文件中
            printStream.println(stringHtml.toString());
        }catch (Exception e) {
            e.printStackTrace();
            }
        System.out.println("成功生成测试报告："+reportPath);
    }

    public static void main(String[] args){
        Report report=new Report();
        List<List<String>> reportInfo =new ArrayList<List<String>>();
        List<String> list1 =new ArrayList<String>();
        list1.add("用例1");
        list1.add("定位失败");
        list1.add("Expected condition failed: waiting for presence of element located by: By.id: 9 (tried for 3 second(s) with 500 MILLISECONDS interval)" + "Build info: version: '3.0.1', revision: '1969d75', time: '2016-10-18 09:49:13 -0700'" +
                "System info: host: 'DESKTOP-QNL5NQU', ip: '172.56.33.121', os.name: 'Windows 10', os.arch: 'amd64', os.version: '10.0', java.version: '9.0.1'" +
                "Driver info: org.openqa.selenium.chrome.ChromeDriver" +
                "Capabilities [{applicationCacheEnabled=false, rotatable=false, mobileEmulationEnabled=false, networkConnectionEnabled=false, chrome={chromedriverVersion=2.33.506120 (e3e53437346286c0bc2d2dc9aa4915ba81d9023f), userDataDir=C:\\Users\\suhui\\AppData\\Local\\Temp\\scoped_dir7904_19325}, takesHeapSnapshot=true, pageLoadStrategy=normal, databaseEnabled=false, handlesAlerts=true, hasTouchScreen=false, version=63.0.3239.84, platform=XP, browserConnectionEnabled=false, nativeEvents=true, acceptSslCerts=true, locationContextEnabled=true, webStorageEnabled=true, browserName=chrome, takesScreenshot=true, javascriptEnabled=true, cssSelectorsEnabled=true, setWindowRect=true, unexpectedAlertBehaviour=}]" +
                "Session ID: de0ccac8b077dbc2fba9617dc4735d82");
        List<String> list2 =new ArrayList<String>();
        list2.add("用例2");
        list2.add("断言失败");
        list2.add("失败原因2断言失败");
        List<String> list3 =new ArrayList<String>();
        list3.add("用例3");
        list3.add("通过");
        reportInfo.add(list1);
        reportInfo.add(list2);
        reportInfo.add(list3);
//        report.creatReport(reportInfo,3,1,0,0,
//                "C:\\Users\\suhui\\Desktop\\testReport主函数测试.html","主函数测试","主函数测试",false);
        report.creatReport(reportInfo,3,1,0,0,
                "report/testReport主函数测试.html","主函数测试","主函数测试",false);

        System.out.println("reportInfo>>>>>>>>>>>>>>>>>");
        System.out.println(reportInfo.toString());
    }


}
