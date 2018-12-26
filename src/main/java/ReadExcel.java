import com.alibaba.fastjson.JSON;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

//import org.apache.poi.xssf.usermodel.*;

public class ReadExcel {
    String excelPath;//测试数据excel路径
    XSSFSheet sheet;//excel对象
    String sheetName;//sheet名称
    List<String> firstCellOfRowsList=new ArrayList<String>();;//sheet页第一列所有字段集合
    List<String> keysOfExcelMap;
    Map<String,List<String>> sheetMap;//key（String）是该行第一个字段，value（list）是该行所有字段

    List<List<List<String>>> excelList;//
    List<String> sheetNamesList;

    //无参构造
    public ReadExcel(){}
    //含参构造
    public ReadExcel(String excelPath){
        this.excelPath=excelPath;
    }
    //含参构造
    public ReadExcel(String excelPath,String sheetName){
        this.excelPath=excelPath;
        this.sheetName=sheetName;
    }

    /*
    以List返回整个excel数据
    excel路径都为全局变量，在构造函数中初始化
    String sheetTOTest是excel中要测试的sheet页名称，多个用“，”隔开
    */
    public List getExcelList(String sheetTOTest) throws IOException {
        sheetNamesList= new ArrayList();
        //sheetTOTest转list
        List<String> sheetTOTestList= Arrays.asList(sheetTOTest.split("，"));
        excelList=new ArrayList<List<List<String>>>();

        XSSFWorkbook workbook=null ;
        try{
            FileInputStream excelFileInputStream = new FileInputStream(excelPath);// 创建 Excel 文件的输入流对象
            workbook = new XSSFWorkbook(excelFileInputStream);// 创建其对象，就打开这个 Excel 文件
            excelFileInputStream.close();//关闭输入流
        }catch (Exception e){
            System.out.println("创建excel表格对象出错，请检查配置文件prop.properties中文件的excePath");
            throw new RuntimeException(e);

        }


//        FileInputStream excelFileInputStream = new FileInputStream(excelPath);// 创建 Excel 文件的输入流对象
//        XSSFWorkbook workbook = new XSSFWorkbook(excelFileInputStream);// 创建其对象，就打开这个 Excel 文件
//        excelFileInputStream.close();//关闭输入流

        //excel中的全部sheet名称放入allSheetNamesList集合中
        List<String> allSheetNamesList= new ArrayList();
        for (int i = 0; i <workbook.getNumberOfSheets(); i++) {
            allSheetNamesList.add(workbook.getSheetName(i));
        }
        System.out.println("excel中的所有sheet(allSheetNamesList)："+allSheetNamesList);
        System.out.println("配置文件中要测试的sheet名称（sheetTOTestList）："+sheetTOTestList);

        //判断配置文件prop.properties中的给sheetTOTest配置的要测试的sheet名称是否存在
        for(int z=0;z<sheetTOTestList.size();z++){
            if(allSheetNamesList.contains(sheetTOTestList.get(z))){
                sheetNamesList.add(sheetTOTestList.get(z));
            }else {
                System.out.println("excel中没有sheet页："+sheetTOTestList.get(z));
            }
        }



//        sheetNamesList= new ArrayList();
//        for (int i = 0; i <workbook.getNumberOfSheets(); i++) {
//            sheetNamesList.add(workbook.getSheetName(i));
//        }
        System.out.println("将要测试的sheet："+sheetNamesList);




        for(int i=0;i<sheetNamesList.size();i++){//第一层遍历，遍历excel表中的sheet页
            String  thisSheetName= sheetNamesList.get(i);
            sheet= workbook.getSheetAt(allSheetNamesList.indexOf(thisSheetName));// XSSFSheet 代表 Excel 文件中的一张表格，通过 getSheetAt(0) 指定表格索引来获取对应表格
            List<List<String>> sheetList=new ArrayList<List<String>>();//创建代表sheet页的List
            for(int rowIndex=0;rowIndex<sheet.getLastRowNum()+1;rowIndex++){//第二层遍历：遍历sheet页中的每一行
               //System.out.println("开始获取表："+sheet.getSheetName()+" 的第 "+rowIndex+" 行");
                XSSFRow row = sheet.getRow(rowIndex);//XSSFRow 代表一行数据
                List<String> list =new ArrayList<String>();//创建代表行的list
                for(int cellIndex=0;cellIndex<row.getLastCellNum();cellIndex++){//第三层遍历：遍历该行的每一个单元格
//                    System.out.println("处理表："+sheet.getSheetName()+"的第 "+rowIndex+" 行的第 "+cellIndex+" 单元格");
                    String aa=null;
                    try{
                        aa=row.getCell(cellIndex).toString();//获得该行每一个单元格的数据
                    }catch(Exception e){
                        System.out.println("读取第"+(i+1)+"张sheet:"+thisSheetName+"，第"+(rowIndex+1)+"行，第"+cellIndex+"个单元格出错");
                        throw new RuntimeException(e);

                    }
                    list.add(aa);//将每一个单元格的数据添加到list
                }
                sheetList.add(list);
            }
            excelList.add(sheetList);
        }
        return excelList;
    }

    /*
    返回excel中某个sheet的Map
    sheet名字和excel路径都为全局变量，都在构造函数中初始化
    */
    public Map getSheetMap() throws IOException {
        FileInputStream excelFileInputStream = new FileInputStream(excelPath);// 创建 Excel 文件的输入流对象
        XSSFWorkbook workbook = new XSSFWorkbook(excelFileInputStream);// 创建其对象，就打开这个 Excel 文件
        excelFileInputStream.close();//关闭输入流
        //excel中的全部sheet名称放入sheetNamesList集合中
        keysOfExcelMap =new ArrayList<String>();
        for (int i = 0; i <workbook.getNumberOfSheets(); i++) {
            keysOfExcelMap.add(workbook.getSheetName(i));
        }
        //如果sheetNameList中存在ssheetName,则读取该sheet；不存控制台输出信息
        if (keysOfExcelMap.contains(sheetName)){
            sheet= workbook.getSheetAt(keysOfExcelMap.indexOf(sheetName));// XSSFSheet 代表 Excel 文件中的一张表格，通过 getSheetAt(0) 指定表格索引来获取对应表格
            sheetMap=new HashMap<String, List<String>>();
            //遍历循序-先遍历行，再遍历该行的单元格
            for(int rowIndex=0;rowIndex<sheet.getLastRowNum()+1;rowIndex++){//遍历行，获得行对象row
                XSSFRow row = sheet.getRow(rowIndex);//XSSFRow 代表一行数据
                List<String> list=new ArrayList<String>();//实例化该行所有字段的list集合
                for(int cellIndex=0;cellIndex<row.getLastCellNum();cellIndex++){//遍历该行的每一列
                    String aa=null;
                    aa=row.getCell(cellIndex).toString();
                    list.add(aa);//将该所有字段加入list
                    if(cellIndex==0){//如果取值为该行第一列的字段，加入到list firstCellOfRowsList中，方便后续操作使用（因为map无序）
                        firstCellOfRowsList.add(aa);
                    }
                }
                sheetMap.put(row.getCell(0).toString(),list);//将该行第一个单元格的值作为key（string），其他单元格的值组成value（list），加入到map中
            }
        }else{
            System.out.println("不存在sheet页："+sheetName);
        }
        workbook.close();
        return sheetMap;
    }



    //返回标记list，有sheet页的第一列值组成，供监听器类调用
    public Set<String> tag() throws IOException {
        Map<String,List<String>> map=this.getSheetMap();
        return map.keySet();
    }

    //json转map
    public void jsonToMap(){

        String json2="{\"财务\":[\"张三\",\"男\",\"21\",\"副经理\"],\"人力\":[\"李四\",\"男\",\"22\",\"总经理\"]}";
        Map<String,List<String>> map2= JSON.parseObject(json2,Map.class);
        System.out.println(map2.get("财务").get(0));
    }

    public static  void main(String[] args) throws IOException {
        //ReadExcel readExcel=new ReadExcel("C:\\Users\\suhui\\Desktop\\test.xlsx","Sheet3");
        ReadExcel readExcel=new ReadExcel("D:\\备份代码\\autotestDemo20180410\\autotestDemo\\testData\\testxy_IDPlat1.3.xlsx");
//        readExcel.getSheetMap();
//        Set<Map.Entry<String, List<String>>> entrySet=readExcel.sheetMap.entrySet();
//        for(Map.Entry entry:entrySet){
//            System.out.print(entry.getKey()+"-");
//            System.out.println(entry.getValue());
//        }
//        System.out.println("第一列所有字段list："+readExcel.firstCellOfRowsList.toString());
        System.out.println(readExcel.getExcelList("整个模块串联，企业员工"));

    }
}
