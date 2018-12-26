import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

/*这是一个监听器
类名配置在testng.xml里面
对测试用例中的@Test的参数enable进行动态改变*/
public class AddAnnotationTest implements IAnnotationTransformer {
    Set tagsSet;

    public AddAnnotationTest() throws IOException {
        this.tagsSet=new ReadExcel("C:\\Users\\suhui\\Desktop\\test.xlsx","数据").tag();
    }

    @Override
    public void transform(ITestAnnotation iTestAnnotation, Class testClass, Constructor constructor, Method method) {
        System.out.println("监听器AddAnnotationTest，监听脚本中的测试用例："+method.getName());
        if(tagsSet.contains(method.getName())){
            iTestAnnotation.setEnabled(true);
            System.out.println("激活测试用例："+method.getName());
        }

    }
}
