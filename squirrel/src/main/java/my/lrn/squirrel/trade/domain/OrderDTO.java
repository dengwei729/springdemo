package my.lrn.squirrel.trade.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.lrn.squirrel.trade.state.OrderState;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Integer id;

    private OrderState state;

    private Date createTime;

    private Date updateTime;
}
