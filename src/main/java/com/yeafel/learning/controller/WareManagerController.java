package com.yeafel.learning.controller;

import com.yeafel.learning.dataobject.Progress;
import com.yeafel.learning.dataobject.Record;
import com.yeafel.learning.dto.CoursewareDTO;
import com.yeafel.learning.dto.UserDTO;
import com.yeafel.learning.enums.ResultEnum;
import com.yeafel.learning.exception.LearningException;
import com.yeafel.learning.service.CoursewareService;
import com.yeafel.learning.service.RecordService;
import com.yeafel.learning.utils.ResultVOUtil;
import com.yeafel.learning.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �ļ��ϴ������Ȳ���
 * Created by kangyifan on 2018/11/12 21:41
 */
@RestController
@RequestMapping("/courseWare")
@Slf4j
public class WareManagerController {

    @Autowired
    private CoursewareService coursewareService;

    @Autowired
    private RecordService recordService;

    /**
     * �ļ��ϴ�
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(MultipartFile file,HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>(16);
        if (file != null && !file.isEmpty()) {
            try {
                String path = "D:/ideaWorkspace/learning/src/main/resources/static/video/" + file.getOriginalFilename();
                String imgPath = "D:/ideaWorkspace/learning/src/main/resources/static/img/coursewareimg/"+file.getOriginalFilename()+".jpg";
                String imgName = file.getOriginalFilename()+".jpg";
                String fileName = file.getOriginalFilename();

//                String path = request.getSession().getServletContext().getRealPath("/") + file.getOriginalFilename();
                file.transferTo(new File(path));
                WareManagerController.fetchFrame(path,imgPath);
                result.put("code", 200);
                result.put("path", fileName);
                result.put("imgPath",imgName);
                result.put("msg", "��ϲ�����ϴ��ɹ�����");
            } catch (IOException e) {
                result.put("code", -1);
                result.put("msg", "�ļ��ϴ������������ϴ���");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            result.put("code", -1);
            result.put("msg", "δ��ȡ����Ч���ļ���Ϣ���������ϴ�!");
        }

        return result;
    }


    //��������Ƶ·��������ͼ����·��
    public static void fetchFrame(String videofile, String framefile)
            throws Exception {
        long start = System.currentTimeMillis();
        File targetFile = new File(framefile);
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(videofile);
        ff.start();
        int length = ff.getLengthInFrames();
        int i = 0;
        Frame f = null;
        while (i < length) {
            // ȥ��ǰ5֡���������ȫ�ڵ�ͼƬ�����Լ��������
            f = ff.grabImage();
            if ((i > 5) && (f.image != null)) {
                break;
            }
            i++;
        }
        ImageIO.write(FrameToBufferedImage(f), "jpg", targetFile);
        //ff.flush();
        ff.stop();
        System.out.println(System.currentTimeMillis() - start);
    }

    public static BufferedImage FrameToBufferedImage(Frame frame) {
        //����BufferedImage����
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }



    /**
     * ��ȡ�ļ��ϴ�����
     *
     * @param request
     * @return
     */
    @GetMapping("/getUploadProgress")
    public Progress getUploadProgress(HttpServletRequest request) {
        return (Progress) request.getSession().getAttribute("uploadStatus");
    }


    /**
     * �����û����༭����������
     */
    @PostMapping("/save")
    public ResultVO<Null> save(CoursewareDTO coursewareDTO){
        if (coursewareDTO != null && coursewareDTO.getCoursewareId() != null){
            CoursewareDTO updateResult = coursewareService.updateCourseware(coursewareDTO);
            if (updateResult == null){
                throw new LearningException(ResultEnum.UPDATE_ERROR);
            }
        }else {
            CoursewareDTO createResult = coursewareService.create(coursewareDTO);
            if (createResult == null){
                throw new LearningException(ResultEnum.CREATE_ERROR);
            }
        }

        return ResultVOUtil.success();

    }


    /**
     *  �û��б�
     */
    @GetMapping("/list")
    public ResultVO<List<CoursewareDTO>> list(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                        @RequestParam(value = "limit",defaultValue = "10") Integer limit,
                                              CoursewareDTO coursewareDTO) throws UnsupportedEncodingException {
        // HttpServletResponse response)


        Integer count = coursewareService.countCoursewareForPage(coursewareDTO.getCoursewareName(),coursewareDTO.getCourseName());

        /** �˴��������ã�  ��ǰ�˴�������usernameֵ�����룬�����ﴦ�����������.*/
        if (coursewareDTO.getCoursewareName() != null) {
            String fname =  URLDecoder.decode(coursewareDTO.getCoursewareName() , "utf-8");
            coursewareDTO.setCoursewareName(fname);
        }

        if (coursewareDTO.getCourseName() != null) {
            String fname =  URLDecoder.decode(coursewareDTO.getCourseName() , "utf-8");
            coursewareDTO.setCourseName(fname);
        }



        PageRequest request = PageRequest.of(page-1,limit);
        Page<CoursewareDTO> coursewareDTOPage = coursewareService.findByCondition(coursewareDTO.getCoursewareName(),coursewareDTO.getCourseName(),request);

//        response.addHeader("Access-Control-Allow-Origin", "*");   ʹ��nginx��Ҫ��
        return ResultVOUtil.success(coursewareDTOPage.getContent(),count);
    }


    @GetMapping("/findByCoursewareId")
    public ResultVO<CoursewareDTO> findByCoursewareId(Long coursewareId){
        CoursewareDTO coursewareDTO = coursewareService.findByCoursewareId(coursewareId);
        return ResultVOUtil.success(coursewareDTO);
    }


    @PostMapping("/delete")
    public ResultVO<Null> delete(String delitems,Long id){
        /*�ж�ʱ����ɾ����������ɾ��*/
        if (delitems != null) {
            String[] strs = delitems.split(",");
            for (int i = 0; i < strs.length; i++) {
                Long a = Long.parseLong(strs[i]);
                coursewareService.deleteByCoursewareId(a);
            }
        } else {
            coursewareService.deleteByCoursewareId(id);
        }
        return ResultVOUtil.success();
    }


    @PostMapping("/saveVideoRecord")
    public ResultVO<Record>  saveVideoRecord(Record record, HttpSession session){
        Record result = null;

        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if(record.getRecordId() == null ){
            record.setUserId(userDTO.getUserId());
             result = recordService.create(record);
        }else {
            record.setUserId(userDTO.getUserId());
            recordService.updateRecord(record);
        }

        return ResultVOUtil.success(result);

    }



    @GetMapping("/getCurrent")
    public ResultVO<Record> getCurrent(Long coursewareId,Long userId,HttpSession session){
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        Record record = recordService.findByCoursewareIdAndUserId(coursewareId,userDTO.getUserId());
        if (record == null){
            Record record1 = new Record();
            record1.setVideoCurrentTime(0D);
            record = record1;
        }
        return ResultVOUtil.success(record);
    }




}
