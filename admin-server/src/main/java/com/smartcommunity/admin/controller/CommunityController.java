package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区管理控制器，涵盖社区公告、车位管理、投诉处理、访客管理和收费管理等多个子模块。
 * 请求路径前缀：/api/admin/community
 */
@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService service;

    /**
     * 分页查询社区公告列表。
     *
     * @param page 页码，默认 1
     * @param size 每页条数，默认 10
     * @return 公告分页数据
     */
    @GetMapping("/notice/page")
    public Result<?> noticePage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageNotice(page, size)); }

    /**
     * 新增或编辑社区公告。
     *
     * @param n 公告实体
     * @return 操作结果
     */
    @PostMapping("/notice")
    public Result<Void> saveNotice(@RequestBody Notice n) { service.saveNotice(n); return Result.ok(); }

    /**
     * 根据 ID 删除社区公告。
     *
     * @param id 公告 ID
     * @return 操作结果
     */
    @DeleteMapping("/notice/{id}")
    public Result<Void> delNotice(@PathVariable Integer id) { service.deleteNotice(id); return Result.ok(); }

    /**
     * 分页查询车位信息列表。
     *
     * @param page 页码，默认 1
     * @param size 每页条数，默认 10
     * @return 车位分页数据
     */
    @GetMapping("/parking/page")
    public Result<?> parkingPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageParking(page, size)); }

    /**
     * 新增或编辑车位信息。
     *
     * @param p 车位实体
     * @return 操作结果
     */
    @PostMapping("/parking")
    public Result<Void> saveParking(@RequestBody Parking p) { service.saveParking(p); return Result.ok(); }

    /**
     * 查询指定车位的绑定记录（关联的业主）。
     *
     * @param id 车位 ID
     * @return 车位绑定记录列表
     */
    @GetMapping("/parking/{id}/binds")
    public Result<List<ParkingBind>> parkingBinds(@PathVariable Integer id) { return Result.ok(service.getParkingBinds(id)); }

    /**
     * 分页查询投诉建议列表。
     *
     * @param page 页码，默认 1
     * @param size 每页条数，默认 10
     * @return 投诉分页数据
     */
    @GetMapping("/complaint/page")
    public Result<?> complaintPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageComplaint(page, size)); }

    /**
     * 更新投诉处理的进度状态。
     *
     * @param id     投诉 ID
     * @param status 新状态值
     * @return 操作结果
     */
    @PutMapping("/complaint/{id}")
    public Result<Void> complaintStatus(@PathVariable Integer id, @RequestParam Integer status)
    { service.updateComplaintStatus(id, status); return Result.ok(); }

    /**
     * 分页查询访客记录列表。
     *
     * @param page 页码，默认 1
     * @param size 每页条数，默认 10
     * @return 访客分页数据
     */
    @GetMapping("/visitor/page")
    public Result<?> visitorPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageVisitor(page, size)); }

    /**
     * 更新访客记录的状态（如审核通过/拒绝）。
     *
     * @param id     访客记录 ID
     * @param status 新状态值
     * @return 操作结果
     */
    @PutMapping("/visitor/{id}")
    public Result<Void> visitorStatus(@PathVariable Integer id, @RequestParam Integer status)
    { service.updateVisitorStatus(id, status); return Result.ok(); }

    /**
     * 分页查询收费记录列表。
     *
     * @param page 页码，默认 1
     * @param size 每页条数，默认 10
     * @return 收费分页数据
     */
    @GetMapping("/charge/page")
    public Result<?> chargePage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageCharge(page, size)); }

    /**
     * 查询指定收费单的明细。
     *
     * @param chargeNo 收费单号
     * @return 收费明细列表
     */
    @GetMapping("/charge/{chargeNo}/details")
    public Result<List<ChargeDetail>> chargeDetails(@PathVariable String chargeNo)
    { return Result.ok(service.getChargeDetails(chargeNo)); }
}
