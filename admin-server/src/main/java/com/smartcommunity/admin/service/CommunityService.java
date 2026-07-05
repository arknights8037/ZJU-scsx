package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端社区综合服务。
 * 提供通知公告、车位管理、投诉报修、访客管理、缴费管理等模块的查询与操作。
 */
@Service
@RequiredArgsConstructor
public class CommunityService {
    private final NoticeMapper noticeMapper;
    private final ParkingMapper parkingMapper;
    private final ParkingBindMapper parkingBindMapper;
    private final ComplaintMapper complaintMapper;
    private final VisitorMapper visitorMapper;
    private final ChargeMapper chargeMapper;
    private final ChargeDetailMapper chargeDetailMapper;

    // ---- 通知公告 ----
    /**
     * 分页查询通知公告列表，按创建时间倒序排列。
     *
     * @param page 页码
     * @param size 每页条数
     * @return 通知公告分页结果
     */
    public Page<Notice> pageNotice(int page, int size) {
        LambdaQueryWrapper<Notice> w = new LambdaQueryWrapper<>();
        w.orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectPage(new Page<>(page, size), w);
    }
    /**
     * 保存或更新通知公告。
     *
     * @param notice 通知公告对象
     */
    public void saveNotice(Notice notice) { noticeMapper.insertOrUpdate(notice); }

    /**
     * 删除通知公告。
     *
     * @param id 通知公告 ID
     */
    public void deleteNotice(Integer id) { noticeMapper.deleteById(id); }

    // ---- 车位 ----
    /**
     * 分页查询车位列表，按创建时间倒序排列。
     *
     * @param page 页码
     * @param size 每页条数
     * @return 车位分页结果
     */
    public Page<Parking> pageParking(int page, int size) {
        return parkingMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Parking>().orderByDesc(Parking::getCreateTime));
    }
    /**
     * 保存或更新车位信息。
     *
     * @param p 车位对象
     */
    public void saveParking(Parking p) { parkingMapper.insertOrUpdate(p); }

    /**
     * 获取指定车位的绑定车辆列表。
     *
     * @param parkingId 车位 ID
     * @return 绑定车辆列表
     */
    public List<ParkingBind> getParkingBinds(Integer parkingId) {
        return parkingBindMapper.selectList(
                new LambdaQueryWrapper<ParkingBind>().eq(ParkingBind::getParkingId, parkingId));
    }

    // ---- 投诉 ----
    /**
     * 分页查询投诉报修列表，按创建时间倒序排列。
     *
     * @param page 页码
     * @param size 每页条数
     * @return 投诉报修分页结果
     */
    public Page<Complaint> pageComplaint(int page, int size) {
        return complaintMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Complaint>().orderByDesc(Complaint::getCreateTime));
    }
    /**
     * 更新投诉报修的处理状态。
     *
     * @param id     投诉 ID
     * @param status 新的状态值
     */
    public void updateComplaintStatus(Integer id, Integer status) {
        Complaint c = new Complaint(); c.setId(id); c.setComplaintStatus(status);
        complaintMapper.updateById(c);
    }

    // ---- 访客 ----
    /**
     * 分页查询访客记录列表，按创建时间倒序排列。
     *
     * @param page 页码
     * @param size 每页条数
     * @return 访客记录分页结果
     */
    public Page<Visitor> pageVisitor(int page, int size) {
        return visitorMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Visitor>().orderByDesc(Visitor::getCreateTime));
    }
    /**
     * 更新访客记录的处理状态。
     *
     * @param id     访客记录 ID
     * @param status 新的状态值
     */
    public void updateVisitorStatus(Integer id, Integer status) {
        Visitor visitor = new Visitor();
        visitor.setId(id);
        visitor.setVisitorStatus(status);
        visitorMapper.updateById(visitor);
    }

    // ---- 缴费 ----
    /**
     * 分页查询缴费账单列表，按创建时间倒序排列。
     *
     * @param page 页码
     * @param size 每页条数
     * @return 缴费账单分页结果
     */
    public Page<Charge> pageCharge(int page, int size) {
        return chargeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Charge>().orderByDesc(Charge::getCreateTime));
    }
    /**
     * 根据账单编号获取缴费明细。
     *
     * @param chargeNo 账单编号
     * @return 缴费明细列表
     */
    public List<ChargeDetail> getChargeDetails(String chargeNo) {
        return chargeDetailMapper.selectList(
                new LambdaQueryWrapper<ChargeDetail>().eq(ChargeDetail::getChargeNo, chargeNo));
    }
}
