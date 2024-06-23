package server.server.dtos.response;

import lombok.*;
import server.server.dtos.OrderItemDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InoviceOrderResponse {
    //Order details
    private Long orderNumber;
    private Date orderDate;
    private String orderStatus;

    //Buyer details
    private String buyerName;
    private String buyerSurname;
    private String buyerAddress;
    private String buyerEmail;
    private String buyerPhoneNumber;

    //Seller details
    private String sellerName;
    private String sellerSurname;
    private String sellerAddress;
    private String sellerEmail;
    private String sellerAccountNumber;

    //Payment details
    private Long paymentMethodId;

    //Product list details
    private List<OrderItemDTO> orderItemsList;

    public double calculateTotalInvoiceAmount(){
        double total = 0.00;
        for (OrderItemDTO orderItem : orderItemsList) {
            total += orderItem.calculateTotalProductAmount();
        }

        return total;
    }
}
