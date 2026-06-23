package com.smartcommunity.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Page<Notice> pageNotice(int page, int size) {
        LambdaQueryWrapper<Notice> w = new LambdaQueryWrapper<>();
        w.orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectPage(new Page<>(page, size), w);
    }
    public void saveNotice(Notice notice) { noticeMapper.insertOrUpdate(notice); }
    public void deleteNotice(Integer id) { noticeMapper.deleteById(id); }

    // ---- 车位 ----
    public Page<Parking> pageParking(int page, int size) {
        return parkingMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Parking>().orderByDesc(Parking::getCreateTime));
    }
    public void saveParking(Parking p) { parkingMapper.insertOrUpdate(p); }
    public List<ParkingBind> getParkingBinds(Integer parkingId) {
        return parkingBindMapper.selectList(
                new LambdaQueryWrapper<ParkingBind>().eq(ParkingBind::getParkingId, parkingId));
    }

    // ---- 投诉 ----
    public Page<Complaint> pageComplaint(int page, int size) {
        return complaintMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Complaint>().orderByDesc(Complaint::getCreateTime));
    }
    public void updateComplaintStatus(Integer id, Integer status) {
        Complaint c = new Complaint(); c.setId(id); c.setComplaintStatus(status);
        complaintMapper.updateById(c);
    }

    // ---- 访客 ----
    public Page<Visitor> pageVisitor(int page, int size) {
        return visitorMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Visitor>().orderByDesc(Visitor::getCreateTime));
    }

    // ---- 缴费 ----
    public Page<Charge> pageCharge(int page, int size) {
        return chargeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Charge>().orderByDesc(Charge::getCreateTime));
    }
    public List<ChargeDetail> getChargeDetails(String chargeNo) {
        return chargeDetailMapper.selectList(
                new LambdaQueryWrapper<ChargeDetail>().eq(ChargeDetail::getChargeNo, chargeNo));
    }
}
