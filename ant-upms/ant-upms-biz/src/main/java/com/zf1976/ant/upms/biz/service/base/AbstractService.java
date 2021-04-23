package com.zf1976.ant.upms.biz.service.base;

import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.upms.biz.pojo.query.AbstractQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.upms.biz.pojo.query.annotation.Param;
import com.zf1976.ant.upms.biz.service.util.LambdaMethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author mac
 * Create by Ant on 2020/8/31 上午11:37
 */

public abstract class AbstractService<D extends BaseMapper<E>, E> extends ServiceImpl<D, E> {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractService.class);
    protected static final AlternativeJdkIdGenerator JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    protected static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    protected final ThreadLocal<QueryChainWrapper<E>> queryChainWrapperThreadLocal = new ThreadLocal<>() ;
    protected final ThreadLocal<Query<? extends AbstractQueryParam>> requestPageThreadLocal = new ThreadLocal<>();
    public AbstractService() {
        this.removeThreadLocalVariable();
    }


    /**
     * 根据id集合取回对象列表
     *
     * @param idList id集合
     * @return 对象列表
     */
    @Override
    public List<E> listByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectBatchIds(idList);
    }

    /**
     * 分页对象拷贝
     *
     * @param sourcePage 原对象
     * @param translator func
     * @param <S>        目标对象
     * @return 转换结果
     */
    protected <S> IPage<S> mapPageToTarget(IPage<E> sourcePage, Function<E, S> translator) {
        final List<S> target = sourcePage.getRecords()
                                         .stream()
                                         .map(translator)
                                         .collect(Collectors.toList());

        final Page<S> targetPage = new Page<>(sourcePage.getCurrent(),
                                              sourcePage.getSize(),
                                              sourcePage.getTotal(),
                                              sourcePage.isSearchCount());
        return targetPage.setRecords(target);
    }

    /**
     * 列表对象拷贝
     *
     * @param sourceList 原对象
     * @param translator func
     * @param <S>        目标对象
     * @return 转换结果
     */
    protected <S> List<S> mapListToTarget(Collection<E> sourceList, Function<E, S> translator) {
        return sourceList.stream()
                         .map(translator)
                         .collect(Collectors.toList());
    }

    /**
     * 获取行字段
     *
     * @param column column mapper
     * @return field
     */
    protected String getColumn(SFunction<E, ?> column) {
        return LambdaMethodUtils.columnToString(column);
    }

    /**
     * 链式 wrapper 查询
     *
     * @return this
     */
    protected AbstractService<D, E> queryChain() {
        return this.queryChain(ChainWrappers.queryChain(super.baseMapper));
    }

    protected AbstractService<D, E> queryChain(QueryChainWrapper<E> queryChainWrapper) {
        this.queryChainWrapperThreadLocal.set(queryChainWrapper);
        return this;
    }

    /**
     * 确实是否保存ThreadLocal 变量
     */
    private void checkThreadLocalVariable() {
        Assert.notNull(this.queryChainWrapperThreadLocal.get(), "queryChainWrapperThreadLocal failed to initialize");
        Assert.notNull(this.requestPageThreadLocal.get(), "requestPageThreadLocal failed to initialize");
    }


    /**
     * 删除ThreadLocal 保存变量
     */
    private void removeThreadLocalVariable() {
        this.requestPageThreadLocal.remove();
        this.queryChainWrapperThreadLocal.remove();
        Assert.isNull(this.queryChainWrapperThreadLocal.get(), "queryChainWrapperThreadLocal removal of failure");
        Assert.isNull(this.requestPageThreadLocal.get(), "requestPageThreadLocal removal of failure");
    }

    /**
     * 分页查询
     *
     * @return page entity
     */
    public IPage<E> selectPage() {
        Query<? extends AbstractQueryParam> requestPage = Optional.ofNullable(this.requestPageThreadLocal.get())
                                                                  .orElseGet(Query::new);
        QueryChainWrapper<E> queryChainWrapper = Optional.ofNullable(this.queryChainWrapperThreadLocal.get())
                                                          .orElseGet(() -> ChainWrappers.queryChain(super.baseMapper));
        IPage<E> selectPage = this.selectPage(requestPage, queryChainWrapper);
        this.removeThreadLocalVariable();
        return selectPage;
    }

    /**
     * 无条件查询
     *
     * @return list of Entity
     */
    public List<E> selectList(){
        List<E> list = Optional.ofNullable(this.queryChainWrapperThreadLocal.get())
                               .orElseGet(() -> ChainWrappers.queryChain(super.baseMapper))
                               .list();
        this.removeThreadLocalVariable();
        return list;
    }

    /**
     * 获取查询页
     *
     * @param requestPage request page param
     * @return page
     */
    public IPage<E> selectPage(Query<? extends AbstractQueryParam> requestPage, QueryChainWrapper<E> queryChainWrapper) {
        Page<E> configPage = this.getConfigPage(requestPage);
        return queryChainWrapper.page(configPage);
    }

    /**
     * 获取查询页
     *
     * @param requestPage request page param
     * @return page
     */
    public IPage<E> selectPage(Query<? extends AbstractQueryParam> requestPage, LambdaQueryChainWrapper<E> lambdaQueryChainWrapper) {
        Page<E> configPage = this.getConfigPage(requestPage);
        return lambdaQueryChainWrapper.page(configPage);
    }

    /**
     * 分页配置
     *
     * @param query request param
     */
    protected Page<E> getConfigPage(Query<? extends AbstractQueryParam> query) {
        Assert.notNull(query, BusinessMsgState.PARAM_ILLEGAL.getReasonPhrase());
        Page<E> configPage = new Page<>();
        configPage.setOrders(query.getOrders());
        return configPage.setCurrent(query.getPage())
                         .setSize(query.getSize());
    }

    protected Page<E> getConfigPage() {
        return this.getConfigPage(new Query<>());
    }

    /**
     * 设置属性
     *
     * @param map map
     * @param obj obj
     * @param <T> 待确定类型
     */
    protected <T> void setProperties(Map<String, Object> map, T obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
                throw new BusinessException(BusinessMsgState.DOWNLOAD_ERROR);
            }
        }
    }

    /**
     * 链式查询 设置查询参数
     *
     * @param requestPage request page
     * @return this
     */
    public AbstractService<D, E> chainQuery(Query<? extends AbstractQueryParam> requestPage) {
        return this.chainQuery(requestPage, () -> null);
    }

    /**
     * 链式查询 设置查询参数 / 暂时支持这么多 between 只支持时间类型
     *
     * @param requestPage request page
     * @return this
     */
    public AbstractService<D, E> chainQuery(Query<? extends AbstractQueryParam> requestPage, Supplier<QueryChainWrapper<E>> supplier) {
        Assert.notNull(requestPage, BusinessMsgState.PARAM_ILLEGAL.getReasonPhrase());
        AbstractQueryParam param = requestPage.getQuery();
        QueryChainWrapper<E> chainWrapper = supplier.get();
        if (param == null) {
            return this;
        }
        this.requestPageThreadLocal.set(requestPage);
        QueryChainWrapper<E> queryChainWrapper;
        if (chainWrapper == null) {
            queryChainWrapper = this.queryChainWrapperThreadLocal.get();
        } else {
            // 自定义wrapper
            this.queryChainWrapperThreadLocal.set(chainWrapper);
            this.checkThreadLocalVariable();
            queryChainWrapper = chainWrapper;
        }
        for (Field field : param.getClass().getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            String fieldName = StringUtil.camelToUnderline(field.getName());
            Object fieldVal = ReflectionUtils.getField(field, param);
            Param annotation = field.getAnnotation(Param.class);
            // 空值字段直接跳过
            if (ObjectUtils.isEmpty(fieldVal) || annotation == null) {
                continue;
            }
            switch (annotation.type()) {
                case EQ:
                    queryChainWrapper.eq(fieldName, fieldVal);
                    break;
                case NE:
                    queryChainWrapper.ne(fieldName, fieldVal);
                    break;
                case IN:
                    if (fieldVal instanceof Collection) {
                        queryChainWrapper.in(fieldName, (Collection<?>) fieldVal);
                    } else {
                        queryChainWrapper.in(fieldName, fieldVal);
                    }
                    break;
                case LIKE:
                    String[] fields = field.getAnnotation(Param.class).fields();
                    if (ObjectUtils.isEmpty(fields)) {
                        queryChainWrapper.like(fieldName, fieldVal);
                    } else {
                        for (int i = 0; i < fields.length; i++) {
                            if (i == fields.length - 1) {
                                queryChainWrapper.like(StringUtil.camelToUnderline(fields[i]), fieldVal);
                            } else {
                                queryChainWrapper.like(StringUtil.camelToUnderline(fields[i]), fieldVal)
                                                 .or();
                            }
                        }
                    }
                    break;
                case NOT_LIKE:
                    queryChainWrapper.notLike(fieldName, fieldVal);
                    break;
                case BETWEEN:
                    @SuppressWarnings("unchecked")
                    List<Date> list = (List<Date>) fieldVal;
                    assert list.size() == 2;
                    queryChainWrapper.between(fieldName, list.get(0), list.get(1));
                    break;
                default:
                    break;
            }
        }
        return this;
    }

    /**
     * 导出excel
     *
     * @param list     对象list
     * @param response response
     */
    protected void downloadExcel(Collection<Map<String, Object>> list, HttpServletResponse response) {
        String tempPath = SYS_TEM_DIR + JDK_ID_GENERATOR.generateId() + ".xlsx";
        File file = new File(tempPath);
        try (ServletOutputStream out = response.getOutputStream();
             BigExcelWriter writer = ExcelUtil.getBigWriter(file)) {
            // 临时目录
            writer.write(list, true);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
            file.deleteOnExit();
            writer.flush(out);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
    }

    /**
     * 验证字段是否为空
     *
     * @param param request param
     * @return boolean
     */
    protected Boolean checkAllFieldIsNotNull(AbstractQueryParam param){
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!ObjectUtils.isEmpty(ReflectionUtils.getField(field, param))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验相通字段值
     *
     * @param entity entity
     * @param dto dto
     * @param collectionHandler state
     */
    protected void validateFields(E entity, Object dto, Consumer<Collection<Object>> collectionHandler) {
        final Set<Object> collectState = new CopyOnWriteArraySet<>();
        ReflectionUtils.doWithLocalFields(entity.getClass(), entityField -> {
            entityField.setAccessible(true);
            Object entityFieldVal = ReflectionUtils.getField(entityField, entity);
            if (!ObjectUtils.isEmpty(entityFieldVal)) {
                ReflectionUtils.doWithLocalFields(dto.getClass(), dtoField -> {
                    dtoField.setAccessible(true);
                    Object dtoFieldVal = ReflectionUtils.getField(dtoField, dto);
                    if (ObjectUtils.nullSafeEquals(entityField.getType(),dtoField.getType())
                            && !ObjectUtils.isEmpty(dtoFieldVal)
                            && ObjectUtils.nullSafeEquals(entityField.getName(), dtoField.getName())
                            && ObjectUtils.nullSafeEquals(entityFieldVal, dtoFieldVal)
                    ) {
                        collectState.add(entityFieldVal);
                    }
                });
            }
        });
        collectionHandler.accept(collectState);
    }

    /**
     * 更新检查
     *
     * @param entity entity
     */
    protected void updateEntityById(E entity) {
        if (!super.updateById(entity)) {
            throw new BusinessException(BusinessMsgState.VERSION_IS_UPDATE);
        }
    }

    /**
     * 新增检查
     *
     * @param entity entity
     */
    protected void savaEntity(E entity) {
        if (!super.save(entity)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
    }

    /**
     * 删除检查
     *
     * @param ids ids
     */
    protected void deleteByIds(Collection<Long> ids) {
        if (!super.removeByIds(ids)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
    }

}
