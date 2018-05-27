package tjwhm.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tjwhm.model.bean.ClothesBean;

import static tjwhm.model.BaseModel.statement;

public class ClothesModel {
    public ClothesBean getClothesInfo(String sid) throws SQLException {
        ClothesBean clothesBean = new ClothesBean();
        ResultSet result = statement.executeQuery("select * from clothes where sid = " + sid + ";");
        while (result.next()) {
            clothesBean.sid = result.getInt("sid");
            clothesBean.name = result.getString("name");
            clothesBean.brand = result.getString("brand");
            clothesBean.color = result.getString("color");
            clothesBean.size = result.getString("size");
            clothesBean.suitable_crowd = result.getString("suitable_crowd");
            clothesBean.price = result.getDouble("price");
            clothesBean.in_stock = result.getInt("in_stock");
            clothesBean.on_sale = result.getBoolean("on_sale");
        }
        return clothesBean;
    }

    public Boolean updateClothesInfo(String sid, int change) throws SQLException {
        ResultSet res = statement
                .executeQuery("select in_stock from clothes where sid=" + sid + ";");
        res.next();
        int afterChange = change + res.getInt("in_stock");
        if (afterChange < 0) {
            return false;
        }
        statement.execute("update clothes set in_stock="
                + String.valueOf(afterChange)
                + " where sid =" + sid + ";");
        ResultSet temp = statement
                .executeQuery("select in_stock from clothes where sid=" + sid + ";");
        temp.next();
        StringBuilder stringBuilder = new StringBuilder("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat(stringBuilder.toString());
        String date = sdf.format(new Date());
        System.out.println(date);

        if (afterChange == temp.getInt("in_stock")) {
            if (change < 0) {

                ResultSet temp1 = statement
                        .executeQuery("select price from clothes where sid=" + sid + ";");
                temp1.next();
                double price = temp1.getDouble("price");
                statement.execute("insert into record values (\""
                        + date + " \" ,"
                        + sid + ","
                        + String.valueOf(0 - change) + ","
                        + String.valueOf(price) + ","
                        + String.valueOf(price * (0 - change)) + ","
                        + "\"sell\");");
                return true;
            } else {
                ResultSet temp1 = statement
                        .executeQuery("select price from clothes where sid=" + sid + ";");
                temp1.next();
                double price1 = temp1.getDouble("price1");
                statement.execute("insert into record values (\""
                        + date + "\","
                        + sid + ","
                        + String.valueOf(change) + ","
                        + String.valueOf(price1) + ","
                        + String.valueOf(price1 * change) + ","
                        + "\"purchase\");");
                return true;
            }
        }
        return false;

    }

    public List<ClothesBean> getOnSaleClothesInfo() throws SQLException {
        String sql = "select * from clothes where on_sale=true;";
        ResultSet result = statement.executeQuery(sql);
        List<ClothesBean> list = new ArrayList<>();
        while (result.next()) {
            ClothesBean clothesBean = new ClothesBean();
            clothesBean.sid = result.getInt("sid");
            clothesBean.name = result.getString("name");
            clothesBean.brand = result.getString("brand");
            clothesBean.color = result.getString("color");
            clothesBean.size = result.getString("size");
            clothesBean.suitable_crowd = result.getString("suitable_crowd");
            clothesBean.price = result.getDouble("price");
            clothesBean.in_stock = result.getInt("in_stock");
            clothesBean.on_sale = result.getBoolean("on_sale");
            list.add(clothesBean);
        }
        return list;
    }

    public Boolean changeOnSaleStatus(String sid, boolean on_sale) throws SQLException {
        String sql = "select on_sale from clothes where sid=" + sid + ";";
        ResultSet resultSet = statement.executeQuery(sql);
        boolean formalStatus = true;
        while (resultSet.next()) {
            formalStatus = resultSet.getBoolean("on_sale");
        }
        if (formalStatus == on_sale) {
            return false;
        }
        sql = "update clothes set on_sale =" + String.valueOf(on_sale) + " where sid=" + sid + ";";
        statement.execute(sql);
        return true;
    }

    public List getSID(String name, String brand) throws SQLException {
        String sql = "select sid from clothes where name like \"%" +
                name + "%\" and brand like \"%" + brand + "%\";";
        ResultSet resultSet = statement.executeQuery(sql);
        List res = new ArrayList();
        while (resultSet.next()) {
            res.add(resultSet.getInt("sid"));
        }
        return res;
    }

    public List getSID(String name, String brand, String color, String size, String suitable_crowd) throws SQLException {
        String sql = "select sid from clothes where name like \"%" +
                name + "%\" and brand like \"%" + brand + "%\"" +
                "and color like \"%" + color + "%\" and size like \"%" +
                size + "%\" and suitable_crowd like \"%" + suitable_crowd + "%\";";
        ResultSet resultSet = statement.executeQuery(sql);
        List res = new ArrayList();
        while (resultSet.next()) {
            res.add(resultSet.getInt("sid"));
        }
        return res;
    }
}
