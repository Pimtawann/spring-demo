package com.techup.spring_demo.repository; // ✅ ระบุแพ็กเกจ repository ให้ตรงโครงสร้าง

/**
 * ✅ Repository คือชั้นที่คุยกับ Database โดยตรงผ่าน JPA
 * เราใช้การ extends จาก JpaRepository ซึ่งจะสร้าง CRUD สำเร็จรูปทั้งหมดให้
 * โดยไม่ต้องเขียน SQL เองเลย
 */
import org.springframework.data.jpa.repository.JpaRepository;
import com.techup.spring_demo.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
}