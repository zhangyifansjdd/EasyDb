package zyf.easydb;

/**
 * Created by ZhangYifan on 2016/9/28.
 */

public class Where {
    private StringBuilder mBuilder;

    public Where() {
        mBuilder = new StringBuilder("where");
    }

    public void andExpress(Express express) {
        if (mBuilder.length() != 5)
            mBuilder.append("and");
        mBuilder.append(express.toString());
    }

    public void orExpress(Express express) {
        mBuilder.append("or");
        mBuilder.append(express.toString());
    }

    public static class Express {
        private String columnName;
        private String sign;
        private String value;

        public Express(String columnName, String sign, String value) {
            this.columnName = columnName;
            this.sign = sign;
            this.value = value;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(" ");
            builder.append(columnName).append(" ");
            builder.append(sign).append(" ");
            builder.append(value).append(" ");
            return builder.toString();
        }
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }
}
