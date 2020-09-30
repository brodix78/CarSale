package ru.carSale.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import ru.carSale.model.Photo;
import ru.carSale.store.Store;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PhotoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String link = req.getParameter("file");
        if (link != null) {
            String name = link.substring(link.lastIndexOf(File.separator) + 1);
            resp.setContentType("name=" + name);
            resp.setContentType("image");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
            File file = new File(link);
            try (FileInputStream in = new FileInputStream(file)) {
                resp.getOutputStream().write(in.readAllBytes());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        Store store = (Store) getServletContext().getAttribute("store");
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        ArrayList<Photo> photos = new ArrayList<>();
        try {
            List<FileItem> items = upload.parseRequest(req);
            File folder = new File("images");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    Photo photo = store.savePhoto(Photo.of("temp"));
                    String filename = item.getName();
                    filename = photo.getId() + filename.substring(filename.lastIndexOf("."));
                    File file = new File(folder + File.separator + filename);
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(item.getInputStream().readAllBytes());
                        photo.setFile(file.toString());
                    }
                    store.savePhoto(photo);
                    photos.add(photo);
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        try {
            PrintWriter pw = resp.getWriter();
            pw.write(new JSONArray(photos).toString());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
