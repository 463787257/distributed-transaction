import com.alibaba.fastjson.JSON;
import com.btjf.distributed.common.enums.DistributedStatusEnum;

/**
 * @author luol
 * @date 2018/1/31
 * @time 11:56
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class Tttt {

    public static void main(String[] args) {
        Tst t = new Tst();
        //Object[] objects = new Object[]{1,2,DistributedStatusEnum.BEGIN};
        Object[] objects = new Object[]{1,2,"BEGIN"};
        t.setArgs(objects);
        Class[] classes = new Class[]{Integer.class,Integer.class,DistributedStatusEnum.class};
        Enum begin = null;
        for (Class clazz : classes) {
            if (clazz.isEnum()) {
                begin = Enum.valueOf(clazz, "BEGIN");
                //Object begin = JSON.parseObject("BEGIN", clazz);
            }
        }
        t.setParameterTypes(classes);
        t.setTargetClass(String.class);
        String s = JSON.toJSONString(t);
        System.out.println(s);
        Tst tt = JSON.parseObject(s, Tst.class);
        System.out.println(tt.toString());
    }

    static class Tst{
        /**
         * 类名
         * */
        private Class targetClass;

        /**
         * 对应的事物状态
         * */
        private DistributedStatusEnum distributedStatusEnum;

        /**
         * 参数
         * */
        private Class[] parameterTypes;

        /**
         * 参数
         * */
        private Object[] args;

        public Class getTargetClass() {
            return targetClass;
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        public DistributedStatusEnum getDistributedStatusEnum() {
            return distributedStatusEnum;
        }

        public void setDistributedStatusEnum(DistributedStatusEnum distributedStatusEnum) {
            this.distributedStatusEnum = distributedStatusEnum;
        }

        public Class[] getParameterTypes() {
            return parameterTypes;
        }

        public void setParameterTypes(Class[] parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }
    }
}
