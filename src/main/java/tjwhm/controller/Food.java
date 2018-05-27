package tjwhm.controller;

// Created by tjwhm at 2018/5/27 9:59 PM

import java.sql.SQLException;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import tjwhm.model.FoodModel;
import tjwhm.model.bean.BaseBean;

@Path("goods/food/{sid}")
@XmlRootElement
public class Food {

    private final String ERROR = "unknown error";
    FoodModel foodModel = new FoodModel();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean getFoodInfo(@PathParam("sid") String sid) throws SQLException {
        if (sid.equals("list")) {
            return new BaseBean<>(-1, "", foodModel.getOnSaleFoodList());
        }
        return new BaseBean<>(-1, "", foodModel.getFoodInfo(sid));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<Boolean> addFood(@PathParam("sid") String sid,
                                     @FormParam("name") String name,
                                     @FormParam("brand") String brand,
                                     @FormParam("shelf_life") String shelf_life,
                                     @FormParam("origin") String origin,
                                     @FormParam("price") double price,
                                     @FormParam("in_stock") int in_stock,
                                     @FormParam("price1") double price1) throws SQLException {
        if (sid.equals("new")) {
            boolean isSuccess = foodModel.addFood(name, brand, shelf_life, origin, price, in_stock, price1);
            if (isSuccess) {
                return new BaseBean<>(-1, "", true);
            }
        }
        return new BaseBean<>(500, ERROR, null);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<Boolean> changeFoodOnSaleStatus(@PathParam("sid") String sid) throws SQLException {
        boolean isSuccess = foodModel.changeFoodOnSaleStatus(sid);
        if (isSuccess) {
            return new BaseBean<>(-1, "", true);
        }
        return new BaseBean<>(500, ERROR, null);
    }
}
