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
            clothesBean.price1 = result.getDouble("price1");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentMills = System.currentTimeMillis() - 13L * 3600L * 1000L;
        Date current = new Date(currentMills);
        String date = sdf.format(current);
        System.out.println(date);

        if (afterChange == temp.getInt("in_stock")) {
            if (change < 0) {

                ResultSet temp1 = statement
                        .executeQuery("select * from clothes where sid=" + sid + ";");
                temp1.next();
                String brand = temp1.getString("brand");
                double price = temp1.getDouble("price");
                statement.execute("insert into record values (\""
                        + date + " \" ,"
                        + sid + ",\""
                        + brand + "\","
                        + String.valueOf(0 - change) + ","
                        + String.valueOf(price) + ","
                        + String.valueOf(price * (0 - change)) + ","
                        + "\"sell\");");
                return true;
            } else {
                ResultSet temp1 = statement
                        .executeQuery("select * from clothes where sid=" + sid + ";");
                temp1.next();
                double price1 = temp1.getDouble("price1");
                String brand = temp1.getString("brand");
                statement.execute("insert into record values (\""
                        + date + "\","
                        + sid + ",\""
                        + brand + "\","
                        + String.valueOf(change) + ","
                        + String.valueOf(price1) + ","
                        + String.valueOf(price1 * change) + ","
                        + "\"purchase\");");
                return true;
            }
        }
        return false;

    }

    public List<ClothesBean> getOnSaleClothesList() throws SQLException {
        String sql = "select * from clothes where on_sale=true;";
        ResultSet result = statement.executeQuery(sql);
        List<ClothesBean> list = new ArrayList<>();
        getListFromResultSet(result, list);
        return list;
    }

    public List<ClothesBean> getAllClothesList() throws SQLException {
        String sql = "select * from clothes;";
        ResultSet result = statement.executeQuery(sql);
        List<ClothesBean> list = new ArrayList<>();
        getListFromResultSet(result, list);
        return list;
    }

    private void getListFromResultSet(ResultSet result, List<ClothesBean> list) throws SQLException {
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
            clothesBean.price1 = result.getDouble("price1");
            clothesBean.on_sale = result.getBoolean("on_sale");
            list.add(clothesBean);
        }
    }

    public Boolean changeOnSaleStatus(String sid) throws SQLException {
        String sql = "select on_sale from clothes where sid=" + sid + ";";
        ResultSet resultSet = statement.executeQuery(sql);
        boolean formalStatus = true;
        while (resultSet.next()) {
            formalStatus = resultSet.getBoolean("on_sale");
        }
        sql = "update clothes set on_sale =" + String.valueOf(!formalStatus) + " where sid=" + sid + ";";
        statement.execute(sql);
        return true;
    }

    List<Integer> getSID(String name, String brand) throws SQLException {
        String sql = "select sid from clothes where name like \"%" +
                name + "%\" and brand like \"%" + brand + "%\";";
        ResultSet resultSet = statement.executeQuery(sql);
        List res = new ArrayList();
        while (resultSet.next()) {
            res.add(resultSet.getInt("sid"));
        }
        return res;
    }

    List<Integer> getSID(String name, String brand, String color, String size, String suitable_crowd) throws SQLException {
        String sql = "select sid from clothes where name like \"%" +
                name + "%\" and brand like \"%" + brand + "%\"" +
                "and color like \"%" + color + "%\" and size like \"%" +
                size + "%\" and suitable_crowd like \"%" + suitable_crowd + "%\";";
        ResultSet resultSet = statement.executeQuery(sql);
        List<Integer> res = new ArrayList<>();
        while (resultSet.next()) {
            res.add(resultSet.getInt("sid"));
        }
        return res;
    }

    public Boolean addNewClothes(String name, String brand, String color, String size, String suitable_crowd, double price, int in_stock, double price1) throws SQLException {
        String sql = "select count(sid) as count from clothes;";
        ResultSet resultSet = statement.executeQuery(sql);
        int count = -1;
        while (resultSet.next()) {
            count = resultSet.getInt("count");
        }
        String insert = "insert into clothes values (" + String.valueOf(count) +
                ", \"" + name + "\", \"" + brand + "\", \"" + color + "\", \"" + size + "\", \"" + suitable_crowd + "\", "
                + String.valueOf(price) + ", " + String.valueOf(in_stock) + ", " + String.valueOf(price1) + ", true);";

        statement.execute(insert);
        int newCount = -1;
        ResultSet countSet = statement.executeQuery(sql);
        while (countSet.next()) {
            newCount = countSet.getInt("count");
        }
        return newCount == count + 1;

    }

    public List<ClothesBean> getFitClothes(String name, String brand, String color,
                                           String size, String gender, double price_from,
                                           double price_to, int stock_from, int stock_to,
                                           double price1_from, double price1_to, String on_sale) throws SQLException {


        if (name == null) name = "";
        if (brand == null) brand = "";
        if (color == null) color = "";
        if (size == null) size = "";
        if (gender == null) gender = "";
        if (price_to == 0) price_to = 9999.9;
        if (price1_to == 0) price1_to = 9999.9;
        if (stock_to == 0) stock_to = 999;
        if (on_sale == null) on_sale = "";


        String select = "select * from clothes where name like \"%" + name
                + "%\" and brand like \"%" + brand + "%\" and color like \"%" + color +
                "%\" and size like \"%" + size + "%\" and suitable_crowd like \"%" + gender +
                "%\" and price > " + price_from + " and price < " + price_to
                + " and in_stock > " + stock_from + " and in_stock < " + stock_to +
                " and price1 > " + price1_from + " and price1 <" + price1_to
                + " and on_sale like \"%" + on_sale + "%\";";
        ResultSet resultSet = statement.executeQuery(select);

        List<ClothesBean> list = new ArrayList<>();
        while (resultSet.next()) {
            ClothesBean clothesBean = new ClothesBean();
            clothesBean.name = resultSet.getString("name");
            clothesBean.brand = resultSet.getString("brand");
            clothesBean.color = resultSet.getString("color");
            clothesBean.size = resultSet.getString("size");
            clothesBean.suitable_crowd = resultSet.getString("suitable_crowd");
            clothesBean.price = resultSet.getDouble("price");
            clothesBean.in_stock = resultSet.getInt("in_stock");
            clothesBean.price1 = resultSet.getDouble("price1");
            clothesBean.on_sale = resultSet.getBoolean("on_sale");
            list.add(clothesBean);
        }

        return list;
    }
}
