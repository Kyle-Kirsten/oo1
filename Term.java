import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Term {
    private BigInteger deg;
    private BigInteger cof;
    private String var;

    public Term(BigInteger deg, BigInteger cof, String var) {
        this.deg = deg;
        this.cof = cof;
        this.var = var;
    }

    public static Term strToTerm(String str) {
        if (str==null) {
            return null;
        }
        Term res = new Term(BigInteger.valueOf(0), BigInteger.valueOf(1), "x");
        //先压空格
        str.replaceAll("\\s", "");
        if (str.length()==0) {
            return null;
        }
        //还可以检查一下str的格式，这里就不检查了
        //常因子得先用([\+\-]*\d+)匹配，再排除掉指数
        //变量因子的正则表达式[\+\-]*x(\*\*[\+\-]?\d+)?
        //项的正则表达式[\+\-]*(\d+|x(\*\*[\+\-]?\d+)?)(\*(([\+\-]?\d+)|(x(\*\*[\+\-]?\d+)?)))*
        //分隔项的分隔符正则表达式(?<![\*\+\-])([\+\-]+)
        //先检测所有常量因子
        Pattern p = Pattern.compile("([\\+\\-]*)(\\d+)");
        Matcher m = p.matcher(str);
        boolean sign = true;
        while (m.find()) {
            if (m.start()>=2&&str.substring(m.start()-2, m.start()).equals("**")) {
                continue;
            }
            sign = isPos(m.group(1)) ? sign : !sign;
            BigInteger tmp = new BigInteger(m.group(2));
            if (tmp.equals(BigInteger.valueOf(0))) {
                return null;
            }
            res.setCof(res.getCof().multiply(tmp));
        }
        //再检测变量因子
        p = Pattern.compile("([\\+\\-]*)x(\\*\\*([\\+\\-]?\\d+))?");
        m = p.matcher(str);
        while (m.find()) {
            sign = isPos(m.group(1)) ? sign : !sign;
            BigInteger tmp = m.group(3)==null||m.group(3).length()==0 ? BigInteger.valueOf(1) :
                    new BigInteger(m.group(3));
            res.setDeg(res.getDeg().add(tmp));
        }
        //最后校正符号
        if (!sign) {
            res.setCof(res.getCof().negate());
        }
        return res;
    }

    public static boolean isPos(String str) {
        //检测正负号
        if (str==null) {
            return true;
        }
        //先压空格
        str.replaceAll("\\s", "");
        //再匹配+-
        Pattern p = Pattern.compile("([\\+\\-]*)(.*)");
        Matcher m = p.matcher(str);
        boolean res = true;
        if (m.matches()) {
            String g1 = m.group(1);
            for (int i=0; i<g1.length(); i++) {
                res = g1.charAt(i)=='-' ? !res : res;
            }
        }
        return res;
    }

    public String getVar() {
        return var;
    }

    public BigInteger getCof() {
        return cof;
    }

    public BigInteger getDeg() {
        return deg;
    }

    public void setCof(BigInteger cof) {
        this.cof = cof;
    }

    public void setDeg(BigInteger deg) {
        this.deg = deg;
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        BigInteger zero = BigInteger.valueOf(0);
        BigInteger one = BigInteger.valueOf(1);
        BigInteger negOne = BigInteger.valueOf(-1);
        if (cof.equals(zero)) return "";
        else if (deg.equals(zero)) {
            return String.valueOf(cof);
        } else if (cof.equals(one)&&deg.equals(one)) {
            return "x";
        } else if (cof.equals(one)&&!deg.equals(one)) {
            return "x**"+String.valueOf(deg);
        } else if (cof.equals(negOne)&&deg.equals(one)) {
            return "-x";
        } else if (cof.equals(negOne)&&!deg.equals(one)) {
            return "-x**"+String.valueOf(deg);
        } else if (deg.equals(one)) {
            return String.valueOf(cof)+"*x";
        } else {
            return String.valueOf(cof)+"*x**"+String.valueOf(deg);
        }
    }
}
