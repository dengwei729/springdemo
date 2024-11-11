package my.lrn.squirrel.trade.service;

import my.lrn.squirrel.trade.domain.OrderDTO;

@Mapper
public interface OrderDTOMapper {

    int insert(OrderDTO orderDTO);

}