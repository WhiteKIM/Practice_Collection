package egovframework.example.sample.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import egovframework.example.param.DeptParameter;
import egovframework.example.sample.service.DeptVO;

@Service("deptService")
public class DeptService extends EgovAbstractServiceImpl{
	
	@Resource(name = "deptMapper")
	private DeptMapper deptMapper;
	
	public DeptVO getDept(int deptno) {
		return deptMapper.getDept(deptno);
	}
	
	public List<DeptVO> getAll() {
		return deptMapper.getAll();
	}
	
	public void insertDept(DeptParameter deptParameter) {
		deptMapper.insertDept(deptParameter);
	}
}
