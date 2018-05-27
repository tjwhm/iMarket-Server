package tjwhm.controller;

import java.sql.SQLException;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import tjwhm.model.ClothesModel;
import tjwhm.model.bean.BaseBean;
import tjwhm.model.bean.ClothesBean;

@Path("goods/clothes/{sid}")
@XmlRootElement
public class Clothes {

    private final String ERROR = "unknown error";
    private ClothesModel clothesModel = new ClothesModel();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean getClothesInfo(@PathParam("sid") String sid) throws SQLException {
        if (sid.equals("list")) {
            return new BaseBean<>(-1, "", clothesModel.getOnSaleClothesList());
        }
        ClothesBean clothesBean = clothesModel.getClothesInfo(sid);
        return new BaseBean<>(-1, "", clothesBean);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<Boolean> updateClothesInfo(@PathParam("sid") String sid, @FormParam("change") int change) throws SQLException {
        Boolean success = clothesModel.updateClothesInfo(sid, change);
        return new BaseBean<>(-1, "", success);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<Boolean> changeClothesOnSaleStatus(@PathParam("sid") String sid, @FormParam("on_sale") boolean on_sale) throws SQLException {
        Boolean success = clothesModel.changeOnSaleStatus(sid, on_sale);
        return new BaseBean<>(-1, "", success);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<Boolean> addClothes(@PathParam("sid") String sid,
                                        @FormParam("name") String name,
                                        @FormParam("brand") String brand,
                                        @FormParam("color") String color,
                                        @FormParam("size") String size,
                                        @FormParam("suitable_crowd") String suitable_crowd,
                                        @FormParam("price") double price,
                                        @FormParam("in_stock") int in_stock,
                                        @FormParam("price1") double price1) throws SQLException {
        if (sid.equals("new")) {
            boolean isSuccess = clothesModel.addNewClothes(name, brand, color, size, suitable_crowd, price, in_stock, price1);
            if (isSuccess) {
                return new BaseBean<>(-1, "success", null);
            }
        }
        return new BaseBean<>(500, ERROR, null);
    }
}
