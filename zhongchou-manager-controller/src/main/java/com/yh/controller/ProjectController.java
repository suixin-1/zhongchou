package com.yh.controller;

import java.awt.Container;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.common.json.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yh.pojo.Comment;
import com.yh.pojo.Condition;
import com.yh.pojo.ProA;
import com.yh.pojo.ProjectA;
import com.yh.pojo.Projects;
import com.yh.pojo.Projectstype;
import com.yh.pojo.User;
import com.yh.pojo.zhongchouResult;
import com.yh.service.ProjectService;
import com.yh.service.ProjectstypeService;
import com.yh.service.user.UserService;

/**
 * 
 * @author Administrator 所有项目controller
 */

@Controller
public class ProjectController {

	@Autowired
	private ProjectService projectServiceImpl;

	@Autowired
	private ProjectstypeService projectstypeService;

	@RequestMapping("/selectAll")
	public String selectAll(HttpServletRequest res) {

		// 查询 项目类型表

		List<Projectstype> selectProjectstypeAll = projectstypeService.selectProjectstypeAll();
		res.setAttribute("projectstypeAll", selectProjectstypeAll);

		String count = res.getParameter("count");
		String pages = res.getParameter("pages");
		int page = 1;
		if (pages == null) {
			pages = "1";
			page = Integer.parseInt(pages);
		} else {
			page = Integer.parseInt(pages);
		}
		int coun = 5;
		if (count == null) {
			count = "5";
			coun = Integer.parseInt(count);
		} else {
			coun = Integer.parseInt(count);
		}

		String pstName = res.getParameter("pstName"); // 项目类型
		String psName = res.getParameter("psName");// 项目名称
		String psTyp = res.getParameter("psType");
		System.out.println(pstName + "............................");
		String usName = res.getParameter("usName");// 发起人名称
		Condition c = new Condition();
		c.setPstName(pstName);
		c.setPsName(psName);
		c.setUsName(usName);
		PageHelper.startPage(page, coun);
		if (psTyp != null) {

			// 判断项目类型
			if (!pstName.equals("0")) {
				c.setPsPstId(Integer.parseInt(pstName));
				System.out.println("1111111111111111111111111");
			}

			if (psTyp.equals("项目状态")) {
				psTyp = null;

				if (pstName != "" || psName != "" || usName != "") {
					System.out.println("222222222222222222222222222");
					System.out.println("------------------------------");
					List<ProjectA> p = projectServiceImpl.selectByKey(c);
					PageInfo<ProjectA> pageInfo = new PageInfo<ProjectA>(p);
					res.setAttribute("pb", pageInfo);
					res.setAttribute("list", p);
					return "project/products";
				} else {
					System.out.println("33333333333333333333333333");
					List<ProjectA> selectAll = projectServiceImpl.selectAll();
					PageInfo<ProjectA> pageInfo = new PageInfo<ProjectA>(selectAll);
					System.out.println(pageInfo);
					res.setAttribute("pb", pageInfo);
					res.setAttribute("list", selectAll);
					return "project/products";
				}
			} else {
				if (psTyp.equals("众筹中")) {
					psTyp = "2";

				} else if (psTyp.equals("众筹失败")) {
					psTyp = "4";

				} else if (psTyp.equals("众筹成功")) {
					psTyp = "3";

				} else if (psTyp.equals("待审核")) {
					psTyp = "0";

				} else if (psTyp.equals("待上架")) {

					psTyp = "1";

				} else if (psTyp.equals("审核未通过")) {
					psTyp = "5";

				}
				System.out.println("444444444444444444444");
				c.setPsType(Integer.parseInt(psTyp));
				List<ProjectA> p = projectServiceImpl.selectByKey(c);
				PageInfo<ProjectA> pageInfo = new PageInfo<ProjectA>(p);
				res.setAttribute("pb", pageInfo);
				res.setAttribute("list", p);
				return "project/products";

			}
		}
		System.out.println("55555555555555555555555555555555");

		List<ProjectA> selectAll = projectServiceImpl.selectAll();
		PageInfo<ProjectA> pageInfo = new PageInfo<ProjectA>(selectAll);
		res.setAttribute("pb", pageInfo);
		res.setAttribute("list", selectAll);

		return "project/products";

	}

	@RequestMapping("/selectById")
	public String selectById(HttpServletRequest res) {
		String ids = res.getParameter("id");
		int id = Integer.parseInt(ids);

		Projects findById = projectServiceImpl.findById(id);

		User selectById = projectServiceImpl.selectById(id);

		ProA selectByPstId = projectServiceImpl.selectByPstId(id);

		Integer psPstId = findById.getPsPstId();
		Projectstype selectByUsId = projectServiceImpl.selectByUsId(psPstId);

		res.setAttribute("fbd", findById);
		res.setAttribute("sbd", selectById);
		res.setAttribute("sbpd", selectByPstId);
		res.setAttribute("sbud", selectByUsId);

		return "project/product-info1";
	}

	// 项目审核页面
	@RequestMapping("/selectType")
	public String selectType(HttpServletRequest res) {
		System.out.println("ssssssssssssssss");
		String count = res.getParameter("count");
		String pages = res.getParameter("pages");
		int page = 1;
		if (pages == null) {
			pages = "1";
			page = Integer.parseInt(pages);
		} else {
			page = Integer.parseInt(pages);
		}
		int coun = 5;
		if (count == null) {
			count = "5";
			coun = Integer.parseInt(count);
		} else {
			coun = Integer.parseInt(count);
		}
		PageHelper.startPage(page, coun);
		List<ProjectA> selectAll = projectServiceImpl.selectByKeyByPsType();
		System.out.println(selectAll);
		// List<ProjectA> sd = new ArrayList<>();
		/*
		 * for (ProjectA projectA : selectAll) {
		 * if(projectA.getPsType().equals("0")){ projectA.setPsType("审核中");
		 * System.out.println("cccc"+projectA); sd.add(projectA); } }
		 */
		PageInfo<ProjectA> pageInfo = new PageInfo<ProjectA>(selectAll);
		res.setAttribute("pb", pageInfo);
		res.setAttribute("list", selectAll);
		return "project/audit_status";

	}

	// 跳转到项目审核详情页面
	@RequestMapping("/selectByPsId")
	public String goProjectdetails(HttpServletRequest req, Model model) {

		// 取用户表id
		String usid = req.getParameter("usid");
		int uid = Integer.parseInt(usid);
		User user = projectServiceImpl.selectById(uid);
		model.addAttribute("list", user);
		// 取项目标id
		String psid = req.getParameter("psid");
		int pid = Integer.parseInt(psid);
		System.out.println(pid + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		Projects projects = projectServiceImpl.findById(pid);
		System.out.println(projects.getPsCustName() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		model.addAttribute("list1", projects);
		return "projectdetails";

	}

	// 审核通过
	@RequestMapping("/updateById")
	public String updateById(HttpServletRequest res) {
		String ids = res.getParameter("id");
		int id = Integer.parseInt(ids);
		projectServiceImpl.updateById(id);
		String selectType = selectType(res);
		return selectType;

	}

	// 审核不通过
	@RequestMapping("/updateByPsId")
	public String updateByPsId(HttpServletRequest res) {
		String psid = res.getParameter("id");
		int id = Integer.parseInt(psid);
		projectServiceImpl.updateByPsId(id);
		String selectType = selectType(res);
		return selectType;

	}

	@RequestMapping("/selectSchedule")
	public String selectSchedule(HttpServletRequest res) {
		String ids = res.getParameter("id");
		int id = Integer.parseInt(ids);

		Projects findById = projectServiceImpl.findById(id);

		res.setAttribute("fbd", findById);

		return "project/project-schedule";

	}

	@RequestMapping("/selectMessage")

	public String selectMessage(HttpServletRequest res) {
		String ids = res.getParameter("id");
		int id = Integer.parseInt(ids);

		Projects findById = projectServiceImpl.findById(id);

		User selectById = projectServiceImpl.selectById(id);

		ProA selectByPstId = projectServiceImpl.selectByPstId(id);

		Integer psPstId = findById.getPsPstId();
		Projectstype selectByUsId = projectServiceImpl.selectByUsId(psPstId);

		res.setAttribute("fbd", findById);
		res.setAttribute("sbd", selectById);
		res.setAttribute("sbpd", selectByPstId);
		res.setAttribute("sbud", selectByUsId);

		return "project/project-message";
	}

	@RequestMapping("/selectComment")

	public String selectComment(HttpServletRequest res) {

		String ids = res.getParameter("id");
		int id = Integer.parseInt(ids);
		Projects findById = projectServiceImpl.findById(id);
		List<Comment> c = projectServiceImpl.selectComment(id);

		res.setAttribute("list", c);
		res.setAttribute("fbd", findById);

		return "project-comment";
	}

}
