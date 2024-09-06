package egovframework.example.sample.service.impl;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import egovframework.example.param.DeptParameter;
import egovframework.example.sample.service.DeptVO;

@Mapper("deptMapper")
public interface DeptMapper {

	DeptVO getDept(int deptno);
	void insertDept(DeptParameter deptParameter);
	List<DeptVO> getAll();
}
