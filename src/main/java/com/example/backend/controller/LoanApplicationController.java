package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoanApplication;
import com.example.backend.pojo.LoginUser;
import com.example.backend.pojo.Policy;
import com.example.backend.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/loan")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @PostMapping("/addloan")
    public ResponseResult addLoanApplication(
            @RequestParam("loanAmount") Integer loanAmount,
            @RequestParam("bankCard") String bankCard,
            @RequestParam("content") String content,
            @RequestParam("type") Integer type,
            @RequestParam("images") List<MultipartFile> images) {

        // Prepare image storage paths
        String uploadDir = "D:/WRW/Frontend/vue-system/src/images";
        List<String> imagePaths = images.stream().map(file -> {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("文件名字为空");
            }

            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

            String uuidFileName = UUID.randomUUID().toString() + fileExtension;

            try {
                Files.copy(file.getInputStream(), Paths.get(uploadDir, uuidFileName));
                return uuidFileName;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("存储文件失败" + originalFilename);
            }
        }).collect(Collectors.toList());

        String imgs = String.join(",", imagePaths);

        //获取当前正在进行请求的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setUserId(loginUser.getUser().getId());
        loanApplication.setApplicant(loginUser.getUser().getRealName());
        loanApplication.setLoanAmount(loanAmount);
        loanApplication.setBankCard(bankCard);
        loanApplication.setIdCard(loginUser.getUser().getIdCard());
        loanApplication.setContent(content);
        loanApplication.setType(type);
        loanApplication.setSchool(loginUser.getUser().getSchool());
        loanApplication.setImgs(imgs);
        loanApplication.setStatus(0); // Status: 0 (Pending review)
        loanApplication.setCreateTime(LocalDateTime.now());
        loanApplication.setUpdateTime(LocalDateTime.now());

        return loanApplicationService.save(loanApplication);
    }

    @PostMapping("/getallloan")
    public ResponseResult getAllLoan(@RequestParam("page") Integer page, @RequestParam("applicant") String applicant, @RequestParam("status") Integer status, @RequestParam("school")String school){
        int size = 12;
        Page<LoanApplication> loanPage = new Page<>(page, size);
        IPage<LoanApplication> resultPage = loanApplicationService.findAllPolicies(loanPage,applicant,status,school);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    //图表展示所用数据
    @PostMapping("/getallloans")
    public ResponseResult getAllLoans(){
        return loanApplicationService.getAllLoans();
    }

    //修改贷款状态,同时添加记录到审核记录表
    @PostMapping("/updatestatus")
    public ResponseResult updateStatus(@RequestParam("id")Long id,@RequestParam("status")Integer status,@RequestParam("reviewComments")String reviewComments){
        return loanApplicationService.updateStatus(id,status,reviewComments);
    }

    //获取已经通过的贷款申请
    @PostMapping("/getallloantrue")
    public ResponseResult getAllLoanTrue(@RequestParam("page") Integer page, @RequestParam("applicant") String applicant, @RequestParam("school")String school){
        int size = 12;
        Page<LoanApplication> loanPage = new Page<>(page, size);
        IPage<LoanApplication> resultPage = loanApplicationService.getTrue(loanPage,applicant,school);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    //人工发放已经通过审核的贷款
    @PostMapping("/distributemoney")
    public ResponseResult distributeMoney(@RequestParam("id")Long id){
        return loanApplicationService.distribute(id);
    }

    //查找自己的贷款申请记录
    @PostMapping("/getbyself")
    public ResponseResult getAllBySelf(@RequestParam("page") Integer page,@RequestParam(value = "status",required = false)Integer status){
        int size = 12;
        Page<LoanApplication> loanPage = new Page<>(page, size);
        IPage<LoanApplication> resultPage = loanApplicationService.getBySelf(loanPage,status);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }
}
