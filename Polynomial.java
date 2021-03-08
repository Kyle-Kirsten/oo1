import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Polynomial {
    private Map<BigInteger, Term> degToTerm;

    public Polynomial() {
        degToTerm = new TreeMap<>();
    }

    public Polynomial(String inStr) {
        degToTerm = new TreeMap<>();
        if (inStr == null) {
            return;
        }
        //先去空格
        String str = inStr.replaceAll("\\s", "");
        if (str.length() == 0) {
            return;
        }
        //项的正则表达式[\+\-]*(\d+|x(\*\*[\+\-]?\d+)?)(\*(([\+\-]?\d+)|(x(\*\*[\+\-]?\d+)?)))*
        //分隔项的分隔符正则表达式(?<!\*)([\+\-]+)包括开头
        //先按分隔项符遍历多项式
        Pattern p = Pattern.compile("(?<![*+\\-])([+\\-]+)");
        Matcher m = p.matcher(str);
        //记录上一个分隔符的起始位置
        int lastFind = 0;
        while (m.find()) {
            if (m.start() == 0) {
                continue;
            }
            this.add(Term.strToTerm(str.substring(lastFind, m.start())));
            lastFind = m.start();
        }
        //加入最后一项
        this.add(Term.strToTerm(str.substring(lastFind)));

    }

    public void add(Term t) {
        if (t == null) {
            return;
        }
        Term tmp = degToTerm.get(t.getDeg());
        if (tmp == null) {
            if (!t.getCof().equals(BigInteger.valueOf(0))) {
                degToTerm.put(t.getDeg(), t);
            }
        } else {
            BigInteger i = tmp.getCof().add(t.getCof());
            if (i.equals(BigInteger.valueOf((0)))) {
                degToTerm.remove(t.getDeg());
            } else {
                tmp.setCof(i);
            }
        }
    }

    public Polynomial derivative() {
        Polynomial der = new Polynomial();
        for (BigInteger i : degToTerm.keySet()) {
            Term t = degToTerm.get(i);
            if (!t.getDeg().equals(BigInteger.valueOf(0))) {
                der.add(new Term(t.getDeg().subtract(BigInteger.valueOf(1)),
                        t.getCof().multiply(t.getDeg()), t.getVar()));
            }
        }
        return der;
    }

    @Override
    public String toString() {
        //升序最短输出
        if (degToTerm.size() == 0) {
            return "0";
        }
        String res = "";
        boolean first = true;
        boolean sign = true;
        for (BigInteger i : degToTerm.keySet()) {
            if (first) {
                res = degToTerm.get(i).toString();
                sign = degToTerm.get(i).getCof().compareTo(BigInteger.valueOf(0)) > 0;
                first = false;
            } else {
                if (sign) {
                    res = degToTerm.get(i).getCof().compareTo(BigInteger.valueOf(0)) > 0 ? res +
                            "+" : res;
                    res = res + degToTerm.get(i).toString();
                } else {
                    if (degToTerm.get(i).getCof().compareTo(BigInteger.valueOf(0)) > 0) {
                        res = degToTerm.get(i).toString() + res;
                        sign = true;
                    } else {
                        res = res + degToTerm.get(i).toString();
                    }
                }
            }
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Polynomial)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        Polynomial that = (Polynomial) o;
        return Objects.equals(this.degToTerm, that.degToTerm);
    }
}
