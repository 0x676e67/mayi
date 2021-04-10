package com.zf1976.ant.upms.biz.pojo.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.power.common.util.StringUtil;
import lombok.ToString;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author mac
 * @date 2020/10/22 9:11 下午
 */
public class Query<T extends AbstractQueryParam> implements Serializable {

    public static final int MAX_SIZE = 1000;
    public static final int MIN_PAGE = 0;

    /**
     * 起始页
     */
    private int page;

    /**
     * 每页数
     */
    private int size;

    /**
     * 排序
     */
    private List<String> sort;

    /**
     * 排序条目
     */
    private List<OrderItem> orders;

    /**
     * 查询参数
     */
    private T query;

    public int getPage() {
        return Math.max(this.page, MIN_PAGE);
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        if (this.size <= 0) {
            return MAX_SIZE;
        }
        return Math.min(this.size, MAX_SIZE);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
        this.setOrders(sort);
    }

    public List<OrderItem> getOrders() {
        return this.orders;
    }

    public void setOrders(List<String> sort) {
        this.orders = parser(sort);
    }

    public T getQuery() {
        return query;
    }

    public void setQuery(T query) {
        this.query = query;
    }

    public boolean orderIsEmpty() {
        return ObjectUtils.isEmpty(this.orders);
    }

    private List<OrderItem> parser(List<String> sort){
        orders = new ArrayList<>();
        sort.forEach(str -> {
            String[] array = StringUtils.commaDelimitedListToStringArray(str);
            Direction direction = Direction.fromString(array[1]);
            if (direction.isDescending()) {
                this.orders.add(OrderItem.desc(StringUtil.camelToUnderline(array[0])));
            } else if (direction.isAscending()){
                this.orders.add(OrderItem.asc(StringUtil.camelToUnderline(array[0])));
            }
        });
        return orders;
    }

    public boolean isMaxSize(){
        return this.size > MAX_SIZE;
    }

    public boolean isMinPage(){
        return this.page < 0;
    }

    public enum Direction {

        /**
         * 升序
         */
        ASC,

        /**
         * 降序
         */
        DESC;


        Direction() {}

        public boolean isAscending() {
            return this.equals(ASC);
        }

        public boolean isDescending() {
            return this.equals(DESC);
        }

        public static Direction fromString(String value) {
            try {
                return valueOf(value.toUpperCase(Locale.US));
            } catch (Exception var) {
                throw new IllegalArgumentException(String.format("Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), var);
            }
        }

        public static Optional<Direction> fromOptionalString(String value) {
            try {
                return Optional.of(fromString(value));
            } catch (IllegalArgumentException var2) {
                return Optional.empty();
            }
        }
    }

    @Override
    public String toString() {
        return "RequestPage{" +
                "page=" + page +
                ", size=" + size +
                ", sort=" + sort +
                ", orders=" + orders +
                ", query=" + query +
                '}';
    }
}
