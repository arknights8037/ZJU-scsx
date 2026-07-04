package com.smartcommunity.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcommunity.mall.common.PageResult;
import com.smartcommunity.mall.common.Result;
import com.smartcommunity.mall.entity.*;
import com.smartcommunity.mall.service.CommunityPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 居民端社区服务接口。
 *
 * Controller 这一层只做三件事：
 * 1. 接收前端传来的参数；
 * 2. 调用 Service 完成真正的业务处理；
 * 3. 用 Result 统一包装返回给前端。
 *
 * 这里的 userId 不是前端自己传的，而是 JwtAuthenticationFilter 从登录 Token 中解析后放进 request 的，
 * 这样可以避免用户随便改参数去查别人的账单、车位、报修记录。
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityPortalController {

    private final CommunityPortalService communityService;

    /**
     * 通知公告是公开信息，只查询已发布的公告。
     */
    @GetMapping("/notice/page")
    public Result<PageResult<Notice>> noticePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Notice> result = communityService.pageNotice(page, size);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 查询当前登录居民自己的报修/投诉记录。
     */
    @GetMapping("/complaint/page")
    public Result<PageResult<Complaint>> complaintPage(
            @RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Complaint> result = communityService.pageComplaint(userId, page, size);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 新增报修/投诉。userId 由后端从 Token 中取，前端只需要传 complaintContent。
     */
    @PostMapping("/complaint")
    public Result<Void> createComplaint(@RequestAttribute Integer userId, @RequestBody Complaint complaint) {
        communityService.createComplaint(userId, complaint);
        return Result.ok();
    }

    /**
     * 查询当前居民的访客登记记录。
     */
    @GetMapping("/visitor/page")
    public Result<PageResult<Visitor>> visitorPage(
            @RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Visitor> result = communityService.pageVisitor(userId, page, size);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 新增访客登记。默认状态在 Service 中设置为“待访”。
     */
    @PostMapping("/visitor")
    public Result<Void> createVisitor(@RequestAttribute Integer userId, @RequestBody Visitor visitor) {
        communityService.createVisitor(userId, visitor);
        return Result.ok();
    }

    /**
     * 查询当前居民的物业账单。
     */
    @GetMapping("/charge/page")
    public Result<PageResult<Charge>> chargePage(
            @RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Charge> result = communityService.pageCharge(userId, page, size);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 查询某个账单的明细。Service 会再次校验这个账单是否属于当前用户。
     */
    @GetMapping("/charge/{chargeNo}/details")
    public Result<List<ChargeDetail>> chargeDetails(@RequestAttribute Integer userId, @PathVariable String chargeNo) {
        return Result.ok(communityService.getChargeDetails(userId, chargeNo));
    }

    /**
     * 模拟缴费：把账单状态改为已缴。
     */
    @PutMapping("/charge/{chargeNo}/pay")
    public Result<Void> payCharge(@RequestAttribute Integer userId, @PathVariable String chargeNo) {
        communityService.payCharge(userId, chargeNo);
        return Result.ok();
    }

    /**
     * 查询当前居民名下的车位。
     */
    @GetMapping("/parking/page")
    public Result<PageResult<Parking>> parkingPage(
            @RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Parking> result = communityService.pageParking(userId, page, size);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 查询车位绑定车辆。Service 会校验车位归属，防止越权查看。
     */
    @GetMapping("/parking/{parkingId}/binds")
    public Result<List<ParkingBind>> parkingBinds(@RequestAttribute Integer userId, @PathVariable Integer parkingId) {
        return Result.ok(communityService.getParkingBinds(userId, parkingId));
    }
}
