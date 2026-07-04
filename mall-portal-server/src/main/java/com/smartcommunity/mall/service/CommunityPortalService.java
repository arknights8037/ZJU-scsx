package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.entity.*;
import com.smartcommunity.mall.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 居民端社区服务业务层。
 *
 * 小白可以这样理解分层：
 * - Controller：负责接收请求；
 * - Service：负责业务规则，比如“只能看自己的账单”“报修内容不能为空”；
 * - Mapper：负责真正操作数据库。
 */
@Service
@RequiredArgsConstructor
public class CommunityPortalService {

    /**
     * 防止前端一次请求过大的 size，把数据库查爆。
     */
    private static final int MAX_PAGE_SIZE = 50;

    private final NoticeMapper noticeMapper;
    private final ComplaintMapper complaintMapper;
    private final VisitorMapper visitorMapper;
    private final ChargeMapper chargeMapper;
    private final ChargeDetailMapper chargeDetailMapper;
    private final ParkingMapper parkingMapper;
    private final ParkingBindMapper parkingBindMapper;

    /**
     * 只给居民展示已发布的公告：noticeStatus = 1。
     */
    public Page<Notice> pageNotice(Integer page, Integer size) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getNoticeStatus, 1).orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectPage(page(page, size), wrapper);
    }

    /**
     * 报修/投诉记录必须按 userId 过滤，否则会看到其他居民的数据。
     */
    public Page<Complaint> pageComplaint(Integer userId, Integer page, Integer size) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Complaint::getUserId, userId).orderByDesc(Complaint::getCreateTime);
        return complaintMapper.selectPage(page(page, size), wrapper);
    }

    /**
     * 新建报修：前端只填内容，用户 id 和状态由后端统一设置。
     */
    public void createComplaint(Integer userId, Complaint complaint) {
        if (!StringUtils.hasText(complaint.getComplaintContent())) {
            throw new RuntimeException("请填写事项描述");
        }
        complaint.setId(null);
        complaint.setUserId(userId);
        complaint.setComplaintStatus(0);
        complaint.setComplaintContent(complaint.getComplaintContent().trim());
        complaintMapper.insert(complaint);
    }

    /**
     * 查询当前居民自己的访客记录。
     */
    public Page<Visitor> pageVisitor(Integer userId, Integer page, Integer size) {
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Visitor::getUserId, userId).orderByDesc(Visitor::getVisitorTime);
        return visitorMapper.selectPage(page(page, size), wrapper);
    }

    /**
     * 新建访客登记：状态 0 表示“待访”，物业处理后可改为 1。
     */
    public void createVisitor(Integer userId, Visitor visitor) {
        if (!StringUtils.hasText(visitor.getVisitorObjective()) || visitor.getVisitorTime() == null) {
            throw new RuntimeException("请填写来访目的和时间");
        }
        visitor.setId(null);
        visitor.setUserId(userId);
        visitor.setVisitorStatus(0);
        visitor.setVisitorObjective(visitor.getVisitorObjective().trim());
        visitorMapper.insert(visitor);
    }

    /**
     * 账单同样按 userId 查询，居民只能看自己的缴费记录。
     */
    public Page<Charge> pageCharge(Integer userId, Integer page, Integer size) {
        LambdaQueryWrapper<Charge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Charge::getUserId, userId).orderByDesc(Charge::getCreateTime);
        return chargeMapper.selectPage(page(page, size), wrapper);
    }

    /**
     * 查账单明细前，先确认账单属于当前用户。
     */
    public List<ChargeDetail> getChargeDetails(Integer userId, String chargeNo) {
        Charge charge = getUserCharge(userId, chargeNo);
        if (charge == null) {
            return List.of();
        }
        return chargeDetailMapper.selectList(
            new LambdaQueryWrapper<ChargeDetail>().eq(ChargeDetail::getChargeNo, chargeNo));
    }

    /**
     * 模拟缴费。
     *
     * @Transactional 表示这个方法里的数据库修改要么全部成功，要么失败时回滚。
     * 当前项目只改一个账单状态，加上它是为了让队友知道支付类操作通常要放事务里。
     */
    @Transactional
    public void payCharge(Integer userId, String chargeNo) {
        Charge charge = getUserCharge(userId, chargeNo);
        if (charge == null) {
            throw new RuntimeException("账单不存在");
        }
        if (Integer.valueOf(1).equals(charge.getChargeStatus())) {
            return;
        }
        charge.setChargeStatus(1);
        chargeMapper.updateById(charge);
    }

    /**
     * 查询当前居民名下的车位。
     */
    public Page<Parking> pageParking(Integer userId, Integer page, Integer size) {
        LambdaQueryWrapper<Parking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Parking::getUserId, userId).orderByDesc(Parking::getCreateTime);
        return parkingMapper.selectPage(page(page, size), wrapper);
    }

    /**
     * 查询车位绑定车辆前，先确认这个车位属于当前用户。
     */
    public List<ParkingBind> getParkingBinds(Integer userId, Integer parkingId) {
        Parking parking = parkingMapper.selectById(parkingId);
        if (parking == null || !userId.equals(parking.getUserId())) {
            return List.of();
        }
        return parkingBindMapper.selectList(
            new LambdaQueryWrapper<ParkingBind>().eq(ParkingBind::getParkingId, parkingId));
    }

    /**
     * 根据用户和账单号查账单，是缴费和查明细共同使用的小工具方法。
     */
    private Charge getUserCharge(Integer userId, String chargeNo) {
        return chargeMapper.selectOne(new LambdaQueryWrapper<Charge>()
            .eq(Charge::getUserId, userId)
            .eq(Charge::getChargeNo, chargeNo));
    }

    /**
     * 统一处理分页参数：
     * - page 小于 1 时按第 1 页处理；
     * - size 为空或小于 1 时默认 10 条；
     * - size 最大不超过 MAX_PAGE_SIZE。
     */
    private <T> Page<T> page(Integer page, Integer size) {
        long current = page == null || page < 1 ? 1L : page;
        long pageSize = size == null || size < 1 ? 10L : Math.min(size, MAX_PAGE_SIZE);
        return new Page<>(current, pageSize);
    }
}
