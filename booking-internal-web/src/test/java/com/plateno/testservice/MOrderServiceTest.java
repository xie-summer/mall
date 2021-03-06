package com.plateno.testservice;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.plateno.booking.internal.base.pojo.Order;
import com.plateno.booking.internal.bean.config.Config;
import com.plateno.booking.internal.bean.contants.BookingConstants;
import com.plateno.booking.internal.bean.exception.OrderException;
import com.plateno.booking.internal.bean.request.custom.MOperateLogParam;
import com.plateno.booking.internal.bean.request.custom.MOrderParam;
import com.plateno.booking.internal.bean.request.custom.ModifyOrderParams;
import com.plateno.booking.internal.goods.MallGoodsService;
import com.plateno.booking.internal.goods.vo.ProductSkuBean;
import com.plateno.booking.internal.interceptor.adam.common.bean.ResultVo;
import com.plateno.booking.internal.service.order.MOrderService;
import com.plateno.booking.internal.sms.SMSSendService;
import com.plateno.booking.internal.sms.model.SmsMessageReq;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class MOrderServiceTest {

    @Autowired
    private MOrderService service;

    @Autowired
    private MallGoodsService mallGoodsService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private SMSSendService sendService;



    @Test
    public void testGetProductAndskuStock() throws OrderException, Exception {


        ProductSkuBean productAndskuStock = mallGoodsService.getProductAndskuStock("16");
        System.out.println(productAndskuStock);

    }

    @Test
    public void testSelectRecord() throws Exception {
        MOperateLogParam param = new MOperateLogParam();
        param.setOrderSubNo("O1477461983320905663");
        param.setOperateType(1);
        param.setPlateForm(1);
        param.setMemberId(181295169);
        param.setRemark("sss");
        System.out.println(service.selectOperateLog(param).getData().toString());
    }

    @Test
    public void testRecordOrder() throws OrderException, Exception {
        MOperateLogParam param = new MOperateLogParam();
        param.setOrderCode("O1477461983320905663");
        param.setOrderSubNo("O1477461983320905663");
        param.setOperateType(1);
        param.setPlateForm(1);
        param.setMemberId(181295169);
        param.setRemark("sss");
        service.saveOperateLog(param);
    }


    @Test
    public void testUserRefund() throws OrderException, Exception {

        MOrderParam param = new MOrderParam();
        param.setOrderNo("O1483584283204762088");
        ResultVo<Object> userRefund = service.userRefund(param);
        System.out.println(userRefund);

    }

    @Test
    public void testConsentRefund() throws OrderException, Exception {

        MOrderParam param = new MOrderParam();
        param.setOrderNo("O1484290387880998108");
        ResultVo<Object> userRefund = service.refundOrder(param);
        System.out.println(userRefund);

    }

    @Test
    public void testHandleGateWayefund() throws OrderException, Exception {

        Order order = new Order();
        order.setOrderNo("O1479179291955945541");
        service.handleGateWayefund(order);

    }

    @Test
    public void testSms() throws OrderException, Exception {

        System.out.println(3123123);

        // 发送退款短信
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {

                Logger logger = LoggerFactory.getLogger("httpLogger");

                logger.info("323232323");

                SmsMessageReq messageReq = new SmsMessageReq();
                Map<String, String> params = new HashMap<String, String>();
                messageReq.setPhone("13533048661");
                params.put("orderCode", "12345678");
                params.put("name", "商品");
                params.put("money", "50000");
                params.put("jf", "10");
                messageReq.setParams(params);
                messageReq.setType(Integer.parseInt(Config.SMS_SERVICE_TEMPLATE_NINE));
                Boolean res = sendService.sendMessage(messageReq);
                System.out.println(res);


            }
        });


        Thread.sleep(5000);

    }



    @Test
    public void testModifyOrder() throws OrderException, Exception {
        ModifyOrderParams modifyOrderParams = new ModifyOrderParams();
        modifyOrderParams.setNewStatus(BookingConstants.PAY_STATUS_6);
        modifyOrderParams.setOrderNo("O1478568730093888087");
        modifyOrderParams.setRemark("");
        modifyOrderParams.setOperateUserid("32323");
        modifyOrderParams.setOperateUsername("xiaoming");
        modifyOrderParams.setPlateForm(1);
        ResultVo<Object> modifyOrder = service.modifyOrderLock(modifyOrderParams);

        if (modifyOrder.success()
                && modifyOrderParams.getNewStatus() == BookingConstants.PAY_STATUS_6) {
            MOrderParam orderParam = new MOrderParam();
            orderParam.setOrderNo(modifyOrderParams.getOrderNo());
            orderParam.setMemberId((int) modifyOrder.getData());
            orderParam.setRefundRemark("");
            orderParam.setOperateUserid(modifyOrderParams.getOperateUserid());
            orderParam.setOperateUsername(modifyOrderParams.getOperateUsername());
            orderParam.setPlateForm(modifyOrderParams.getPlateForm());

            try {
                ResultVo<Object> refundOrder = service.refundOrder(orderParam);
                System.out.println(String.format("orderNo:%s, 执行退款，结果：%s",
                        modifyOrderParams.getOrderNo(), refundOrder));
            } catch (Throwable e) {
                System.out.println("退款审核失败:" + modifyOrderParams.getOrderNo());
            }
        }

        System.out.println(modifyOrder);
    }

    @Test
    public void testAdminRefuseRefund() throws OrderException, Exception {

        MOrderParam orderParam = new MOrderParam();
        orderParam.setOrderNo("O1483519528592537419");
        orderParam.setMemberId(181295316);
        orderParam.setOperateUserid("3232323");
        orderParam.setOperateUsername("xiaoming");
        orderParam.setPlateForm(1);
        ResultVo<Object> adminRefuseRefund = service.adminRefuseRefund(orderParam);
        System.out.println(adminRefuseRefund);

    }


    @Test
    public void testLog() throws OrderException, Exception {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {

                Logger logger = LoggerFactory.getLogger("httpLogger");

                logger.info("323232323");

            }
        });


    }

    @Test
    public void testDeleteOrderr() throws OrderException, Exception {


        MOrderParam orderParam = new MOrderParam();
        orderParam.setOrderNo("O1480328556337869185");
        ResultVo<Object> modifydeliverOrder = service.deleteOrder(orderParam);

        System.out.println(modifydeliverOrder);
    }

    @Test
    public void testGetPruSellAmountByPreDay() throws OrderException, Exception {

        ResultVo<Object> pruSellAmountByPreDay = service.getPruSellAmountByPreDay(10);
        System.out.println(pruSellAmountByPreDay);
    }

    @Test
    public void testQuerySkuSoldNum() throws OrderException, Exception {

        ResultVo<Integer> querySkuSoldNum = service.querySkuSoldNum(21);
        System.out.println(querySkuSoldNum);
    }

}
