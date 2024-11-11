package my.lrn.squirrel.trade.state;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.lrn.squirrel.trade.domain.OrderDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderContext {
    public OrderDTO orderDTO;
}