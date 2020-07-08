package app.controller;

import app.model.MyUploadFrom;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MyFileUploadController {
	@InitBinder
	public void initBinder(WebDataBinder dataBinder){
		Object target = dataBinder.getTarget();
		if (target == null){
			return;
		}

		System.out.println(target);
		if (target.getClass() == MyUploadFrom.class){
			dataBinder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		}
	}

	@GetMapping("/")
	public String menu(){
		return "menu";
	}

	@GetMapping("/uploadOneFile")
	public String uploadOneFileHandler(Model model){
		MyUploadFrom myUploadFrom = new MyUploadFrom();
		model.addAttribute("myUploadFrom", model);
		return "uploadOneFile";
	}

	@PostMapping("uploadOneFile")
	public String uploadOneFileHandler(HttpServletRequest request, Model model, @ModelAttribute MyUploadFrom myUploadFrom){
		return this.doUpload(request, model, myUploadFrom);
	}

	@GetMapping("/uploadMultiFile")
	public String uploadMultiUploadHandler(Model model){
		MyUploadFrom myUploadFrom = new MyUploadFrom();
		model.addAttribute("myUploadFrom", myUploadFrom);
		return "uploadMultiFile";
	}

	@PostMapping("uploadMultiFile")
	public String uploadMultiUploadHandler(HttpServletRequest request, Model model, @ModelAttribute MyUploadFrom myUploadFroms){
		return this.doUpload(request, model, myUploadFroms);
	}

	private String doUpload(HttpServletRequest request, Model model, MyUploadFrom myUploadFrom) {
		String description = myUploadFrom.getDescription();
		String uploadRootPath = request.getServletContext().getRealPath("upload");

		File uploadRootDir = new File(uploadRootPath);

		if (!uploadRootDir.exists()){
			uploadRootDir.mkdirs();
		}

		CommonsMultipartFile[] fileDatas = myUploadFrom.getFileDatas();

		Map<File, String> uploadFiles = new HashMap<>();

		for (CommonsMultipartFile fileData: fileDatas){
			String name = fileData.getOriginalFilename();
			System.out.println("Client File Name = " + name);

			if (name != null && name.length() > 0){
				File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);

				try {
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
					stream.write(fileData.getBytes());
					stream.close();
					uploadFiles.put(serverFile, name);
					System.out.println("Write file: " + serverFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		model.addAttribute("description", description);
		model.addAttribute("uploadedFiles", uploadFiles);

		return "uploadResult";
	}
}
