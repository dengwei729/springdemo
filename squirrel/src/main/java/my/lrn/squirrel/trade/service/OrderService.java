package my.lrn.squirrel.trade.service;

import my.lrn.squirrel.trade.domain.OrderDTO;
import my.lrn.squirrel.trade.state.OrderState;

@Service
public class OrderService {

    @Autowired
    OrderDTOMapper orderDTOMapper;

    public int submitOrder(OrderState state) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setState(state);
        orderDTOMapper.insert(orderDTO);
        return 1;
    }
}
