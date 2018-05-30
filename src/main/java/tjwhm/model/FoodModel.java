package tjwhm.model;

// Created by tjwhm at 2018/5/27 10:07 PM

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tjwhm.model.bean.FoodBean;

public class FoodModel extends BaseModel {

    public List<FoodBean> getOnSaleFoodList() throws SQLException {
        String sql = "select * from food where on_sale is true;";
        ResultSet resultSet = statement.executeQuery(sql);
        List<FoodBean> list = new ArrayList<>();
        getListFromResultSet(resultSet, list);
        return list;
    }

    public List<FoodBean> getAllFoodList() throws SQLException {
        String sql = "select * from food;";
        ResultSet resultSet = statement.executeQuery(sql);
        List<FoodBean> list = new ArrayList<>();
        getListFromResultSet(resultSet, list);
        return list;
    }

    private void getListFromResultSet(ResultSet resultSet, List<FoodBean> list) throws SQLException {
        while (resultSet.next()) {
            FoodBean foodBean = new FoodBean();
            foodBean.sid = resultSet.getInt("sid");
            foodBean.name = resultSet.getString("name");
            foodBean.brand = resultSet.getString("brand");
            foodBean.shelf_life = resultSet.getString("shelf_life");
            foodBean.origin = resultSet.getString("origin");
            foodBean.price = resultSet.getDouble("price");
            foodBean.in_stock = resultSet.getInt("in_stock");
            foodBean.price1 = resultSet.getDouble("price1");
            foodBean.on_sale = resultSet.getBoolean("on_sale");
            list.add(foodBean);
        }
    }

    public FoodBean getFoodInfo(String sid) throws SQLException {
        String sql = "select * from food where sid = " + sid + ";";
        ResultSet resultSet = statement.executeQuery(sql);
        FoodBean foodBean = new FoodBean();
        while (resultSet.next()) {
            foodBean.sid = resultSet.getInt("sid");
            foodBean.name = resultSet.getString("name");
            foodBean.brand = resultSet.getString("brand");
            foodBean.shelf_life = resultSet.getString("shelf_life");
            foodBean.origin = resultSet.getString("origin");
            foodBean.price = resultSet.getDouble("price");
            foodBean.in_stock = resultSet.getInt("in_stock");
            foodBean.price1 = resultSet.getDouble("price1");
            foodBean.on_sale = resultSet.getBoolean("on_sale");
        }
        return foodBean;
    }

    public boolean addFood(String name, String brand, String shelf_life, String origin,
                           double price, int in_stock, double price1) throws SQLException {
        String sql = "select count(sid) as count from food;";
        ResultSet resCount = statement.executeQuery(sql);
        int count = -1;
        while (resCount.next()) {
            count = resCount.getInt("count");
        }
        String insert = "insert into food values (" + String.valueOf(1000 + count) +
                ", \"" + name + "\", \"" + brand + "\", \"" + shelf_life + "\", \"" + origin +
                "\", " + String.valueOf(price) + ", " + String.valueOf(in_stock) +
                ", " + String.valueOf(price1) + ", true);";
        statement.execute(insert);
        int judge = -1;
        ResultSet temp = statement.executeQuery(sql);
        while (temp.next()) {
            judge = temp.getInt("count");
        }
        return judge == count + 1;
    }

    public boolean updateFoodStock(String sid, int change) throws SQLException {
        String select = "select * from food where sid = " + sid + ";";
        ResultSet stockRes = statement.executeQuery(select);
        int stock = 0;
        double price = 0;
        double price1 = 0;
        String brand = "";
        while (stockRes.next()) {
            stock = stockRes.getInt("in_stock");
            price = stockRes.getDouble("price");
            price1 = stockRes.getDouble("price1");
            brand = stockRes.getString("brand");
        }

        String sql = "update food set in_stock = " + String.valueOf(stock + change)
                + " where sid = " + sid + ";";
        statement.execute(sql);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        System.out.println(date);
        if (change < 0) {
            String insert = "insert into record values (\"" + date
                    + "\"," + sid + ",\"" + brand + "\"," + String.valueOf(0 - change)
                    + "," + String.valueOf(price) + "," + String.valueOf(price * (0 - change)) + ",\"sell\");";
            statement.execute(insert);
        } else {
            String insert = "insert into record values (\"" + date
                    + "\"," + sid + ",\"" + brand + "\"," + String.valueOf(change)
                    + "," + String.valueOf(price1) + "," + String.valueOf(price1 * change) + ",\"purchase\");";
            statement.execute(insert);
        }
        return true;
    }

    public boolean changeFoodOnSaleStatus(String sid) throws SQLException {
        String sql = "select on_sale from food where sid = " + sid + ";";
        ResultSet statusRes = statement.executeQuery(sql);
        boolean status = false;
        while (statusRes.next()) {
            status = statusRes.getBoolean("on_sale");
        }
        String update = "update food set on_sale = " + String.valueOf(!status) + " where sid=" + sid + ";";
        statement.execute(update);
        ResultSet newStatusSet = statement.executeQuery(sql);
        boolean judged = false;
        while (newStatusSet.next()) {
            judged = newStatusSet.getBoolean("on_sale");
        }
        return judged != status;
    }

    public List<Integer> getSID(String name, String brand, String shelf_life_from, String shelf_life_to,
                                String origin, double low, double high) throws SQLException {
        String select = "select sid from food where name like \"%" + name
                + "%\" and brand like \"%" + brand + "%\" and shelf_life > \"" + shelf_life_from +
                "\" and shelf_life < \"" + shelf_life_to + "\" and origin like \"%" + origin +
                "%\" and price >" + low + " and price < " + high + ";";

        ResultSet resultSet = statement.executeQuery(select);
        List<Integer> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getInt("sid"));
        }

        return list;
    }

    public List<Integer> getSID(String name, String brand) throws SQLException {
        String select = "select sid from food where name like \"%" + name
                + "%\" and brand like \"%" + brand + "%\";";
        ResultSet resultSet = statement.executeQuery(select);
        List<Integer> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getInt("sid"));
        }
        return list;
    }

    public List<FoodBean> getFitFood(String name, String brand, String life_from, String life_to,
                                     String origin, double price_from,
                                     double price_to, int stock_from, int stock_to,
                                     double price1_from, double price1_to, String on_sale) throws SQLException {
        if (name == null) name = "";
        if (brand == null) brand = "";
        if (life_from == null || life_from.isEmpty()) life_from = "1999-09-09 09:09:09";
        if (life_to == null || life_to.isEmpty()) life_to = "2100-09-09 09:09:09";
        if (origin == null) origin = "";
        if (price_to == 0) price_to = 9999.9;
        if (price1_to == 0) price1_to = 9999.9;
        if (stock_to == 0) stock_to = 999;
        if (on_sale == null) on_sale = "";

        String select = "select * from food where name like \"%" + name
                + "%\" and brand like \"%" + brand +
                "%\" and shelf_life > \"" + life_from +
                "\" and shelf_life < \"" + life_to + "\" and origin like \"%" +
                origin + "%\" and price > " + price_from
                + " and price <" + price_to + " and in_stock > " + stock_from
                + " and in_stock < " + stock_to
                + " and price1>" + price1_from + " and price1<" + price1_to + " and on_sale " +
                "like \"%" + on_sale + "%\";";

        ResultSet resultSet = statement.executeQuery(select);
        List<FoodBean> list = new ArrayList<>();
        while (resultSet.next()) {
            FoodBean foodBean = new FoodBean();
            foodBean.name = resultSet.getString("name");
            foodBean.brand = resultSet.getString("brand");
            foodBean.shelf_life = resultSet.getString("shelf_life");
            foodBean.origin = resultSet.getString("origin");
            foodBean.price = resultSet.getDouble("price");
            foodBean.in_stock = resultSet.getInt("in_stock");
            foodBean.price1 = resultSet.getDouble("price1");
            foodBean.on_sale = resultSet.getBoolean("on_sale");
            list.add(foodBean);
        }

        return list;
    }

}
