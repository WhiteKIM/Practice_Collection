package egovframework.example.sample.web;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import egovframework.example.param.DeptParameter;
import egovframework.example.sample.service.DeptVO;
import egovframework.example.sample.service.impl.DeptService;

@Controller
public class DeptController {
	
	@Resource(name="deptService")
	private DeptService deptService;
	
	@RequestMapping(value ="/deptWrite.do")
	public String writeForm() {
		return "dept/deptWrite";
	}
	
	@RequestMapping(value = "deptWriteSave.do", method = RequestMethod.POST)
	public String deptSave(DeptParameter deptvo) {
		deptService.insertDept(deptvo);
		return "";
	}

	@RequestMapping("/deptList.do")
	public String getList(Model model) {
		model.addAttribute("deptlist", deptService.getAll());
		return "dept/deptList";
	}
	
	@RequestMapping("/detail.do")
	public String getDetail(
			@RequestParam("deptno") int deptno,
			Model model
	) {
		
		
		model.addAttribute("dept", deptService.getDept(deptno));
		return "dept/deptDetail";
	}
}
