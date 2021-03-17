package com.ethoca.cart.utils;

import com.ethoca.cart.model.CartProduct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class ServiceUtils {

    public static List<CartProduct> byteToCart(byte[] bytes) throws Exception{
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream is = new ObjectInputStream(in);
        List<CartProduct> cartProducts = (List<CartProduct>) is.readObject();
        return cartProducts;
    }

    public static byte[] cartToByte(List<CartProduct> cartProducts) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(cartProducts);
        return out.toByteArray();
    }
}
