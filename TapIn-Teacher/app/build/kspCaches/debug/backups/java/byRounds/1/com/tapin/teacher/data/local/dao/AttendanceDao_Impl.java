package com.tapin.teacher.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tapin.teacher.data.local.entity.AttendanceRecord;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AttendanceDao_Impl implements AttendanceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AttendanceRecord> __insertionAdapterOfAttendanceRecord;

  public AttendanceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAttendanceRecord = new EntityInsertionAdapter<AttendanceRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `attendance_records` (`local_id`,`session_id`,`student_id`,`student_name`,`student_number`,`tapped_at`,`status`,`synced`,`course_name`,`course_code`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AttendanceRecord entity) {
        statement.bindString(1, entity.getLocalId());
        statement.bindString(2, entity.getSessionId());
        statement.bindString(3, entity.getStudentId());
        statement.bindString(4, entity.getStudentName());
        if (entity.getStudentNumber() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getStudentNumber());
        }
        statement.bindLong(6, entity.getTappedAt());
        statement.bindString(7, entity.getStatus());
        final int _tmp = entity.getSynced() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getCourseName());
        statement.bindString(10, entity.getCourseCode());
      }
    };
  }

  @Override
  public Object insert(final AttendanceRecord record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAttendanceRecord.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AttendanceRecord>> getBySession(final String sessionId) {
    final String _sql = "SELECT * FROM attendance_records WHERE session_id = ? ORDER BY tapped_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"attendance_records"}, new Callable<List<AttendanceRecord>>() {
      @Override
      @NonNull
      public List<AttendanceRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "local_id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "session_id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "student_id");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "student_name");
          final int _cursorIndexOfStudentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "student_number");
          final int _cursorIndexOfTappedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "tapped_at");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "course_name");
          final int _cursorIndexOfCourseCode = CursorUtil.getColumnIndexOrThrow(_cursor, "course_code");
          final List<AttendanceRecord> _result = new ArrayList<AttendanceRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttendanceRecord _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentNumber;
            if (_cursor.isNull(_cursorIndexOfStudentNumber)) {
              _tmpStudentNumber = null;
            } else {
              _tmpStudentNumber = _cursor.getString(_cursorIndexOfStudentNumber);
            }
            final long _tmpTappedAt;
            _tmpTappedAt = _cursor.getLong(_cursorIndexOfTappedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp != 0;
            final String _tmpCourseName;
            _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
            final String _tmpCourseCode;
            _tmpCourseCode = _cursor.getString(_cursorIndexOfCourseCode);
            _item = new AttendanceRecord(_tmpLocalId,_tmpSessionId,_tmpStudentId,_tmpStudentName,_tmpStudentNumber,_tmpTappedAt,_tmpStatus,_tmpSynced,_tmpCourseName,_tmpCourseCode);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getBySessionOnce(final String sessionId,
      final Continuation<? super List<AttendanceRecord>> $completion) {
    final String _sql = "SELECT * FROM attendance_records WHERE session_id = ? ORDER BY tapped_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AttendanceRecord>>() {
      @Override
      @NonNull
      public List<AttendanceRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "local_id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "session_id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "student_id");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "student_name");
          final int _cursorIndexOfStudentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "student_number");
          final int _cursorIndexOfTappedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "tapped_at");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "course_name");
          final int _cursorIndexOfCourseCode = CursorUtil.getColumnIndexOrThrow(_cursor, "course_code");
          final List<AttendanceRecord> _result = new ArrayList<AttendanceRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttendanceRecord _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentNumber;
            if (_cursor.isNull(_cursorIndexOfStudentNumber)) {
              _tmpStudentNumber = null;
            } else {
              _tmpStudentNumber = _cursor.getString(_cursorIndexOfStudentNumber);
            }
            final long _tmpTappedAt;
            _tmpTappedAt = _cursor.getLong(_cursorIndexOfTappedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp != 0;
            final String _tmpCourseName;
            _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
            final String _tmpCourseCode;
            _tmpCourseCode = _cursor.getString(_cursorIndexOfCourseCode);
            _item = new AttendanceRecord(_tmpLocalId,_tmpSessionId,_tmpStudentId,_tmpStudentName,_tmpStudentNumber,_tmpTappedAt,_tmpStatus,_tmpSynced,_tmpCourseName,_tmpCourseCode);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AttendanceRecord>> getAll() {
    final String _sql = "SELECT * FROM attendance_records ORDER BY tapped_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"attendance_records"}, new Callable<List<AttendanceRecord>>() {
      @Override
      @NonNull
      public List<AttendanceRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "local_id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "session_id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "student_id");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "student_name");
          final int _cursorIndexOfStudentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "student_number");
          final int _cursorIndexOfTappedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "tapped_at");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "course_name");
          final int _cursorIndexOfCourseCode = CursorUtil.getColumnIndexOrThrow(_cursor, "course_code");
          final List<AttendanceRecord> _result = new ArrayList<AttendanceRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttendanceRecord _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentNumber;
            if (_cursor.isNull(_cursorIndexOfStudentNumber)) {
              _tmpStudentNumber = null;
            } else {
              _tmpStudentNumber = _cursor.getString(_cursorIndexOfStudentNumber);
            }
            final long _tmpTappedAt;
            _tmpTappedAt = _cursor.getLong(_cursorIndexOfTappedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp != 0;
            final String _tmpCourseName;
            _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
            final String _tmpCourseCode;
            _tmpCourseCode = _cursor.getString(_cursorIndexOfCourseCode);
            _item = new AttendanceRecord(_tmpLocalId,_tmpSessionId,_tmpStudentId,_tmpStudentName,_tmpStudentNumber,_tmpTappedAt,_tmpStatus,_tmpSynced,_tmpCourseName,_tmpCourseCode);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getUnsynced(final Continuation<? super List<AttendanceRecord>> $completion) {
    final String _sql = "SELECT * FROM attendance_records WHERE synced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AttendanceRecord>>() {
      @Override
      @NonNull
      public List<AttendanceRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "local_id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "session_id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "student_id");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "student_name");
          final int _cursorIndexOfStudentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "student_number");
          final int _cursorIndexOfTappedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "tapped_at");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "course_name");
          final int _cursorIndexOfCourseCode = CursorUtil.getColumnIndexOrThrow(_cursor, "course_code");
          final List<AttendanceRecord> _result = new ArrayList<AttendanceRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttendanceRecord _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentNumber;
            if (_cursor.isNull(_cursorIndexOfStudentNumber)) {
              _tmpStudentNumber = null;
            } else {
              _tmpStudentNumber = _cursor.getString(_cursorIndexOfStudentNumber);
            }
            final long _tmpTappedAt;
            _tmpTappedAt = _cursor.getLong(_cursorIndexOfTappedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp != 0;
            final String _tmpCourseName;
            _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
            final String _tmpCourseCode;
            _tmpCourseCode = _cursor.getString(_cursorIndexOfCourseCode);
            _item = new AttendanceRecord(_tmpLocalId,_tmpSessionId,_tmpStudentId,_tmpStudentName,_tmpStudentNumber,_tmpTappedAt,_tmpStatus,_tmpSynced,_tmpCourseName,_tmpCourseCode);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Integer> getPendingSyncCount() {
    final String _sql = "SELECT COUNT(*) FROM attendance_records WHERE synced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"attendance_records"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object countForSession(final String sessionId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM attendance_records WHERE session_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object findDuplicate(final String studentId, final String sessionId,
      final Continuation<? super AttendanceRecord> $completion) {
    final String _sql = "SELECT * FROM attendance_records WHERE student_id = ? AND session_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, studentId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AttendanceRecord>() {
      @Override
      @Nullable
      public AttendanceRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "local_id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "session_id");
          final int _cursorIndexOfStudentId = CursorUtil.getColumnIndexOrThrow(_cursor, "student_id");
          final int _cursorIndexOfStudentName = CursorUtil.getColumnIndexOrThrow(_cursor, "student_name");
          final int _cursorIndexOfStudentNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "student_number");
          final int _cursorIndexOfTappedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "tapped_at");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCourseName = CursorUtil.getColumnIndexOrThrow(_cursor, "course_name");
          final int _cursorIndexOfCourseCode = CursorUtil.getColumnIndexOrThrow(_cursor, "course_code");
          final AttendanceRecord _result;
          if (_cursor.moveToFirst()) {
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpStudentId;
            _tmpStudentId = _cursor.getString(_cursorIndexOfStudentId);
            final String _tmpStudentName;
            _tmpStudentName = _cursor.getString(_cursorIndexOfStudentName);
            final String _tmpStudentNumber;
            if (_cursor.isNull(_cursorIndexOfStudentNumber)) {
              _tmpStudentNumber = null;
            } else {
              _tmpStudentNumber = _cursor.getString(_cursorIndexOfStudentNumber);
            }
            final long _tmpTappedAt;
            _tmpTappedAt = _cursor.getLong(_cursorIndexOfTappedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp != 0;
            final String _tmpCourseName;
            _tmpCourseName = _cursor.getString(_cursorIndexOfCourseName);
            final String _tmpCourseCode;
            _tmpCourseCode = _cursor.getString(_cursorIndexOfCourseCode);
            _result = new AttendanceRecord(_tmpLocalId,_tmpSessionId,_tmpStudentId,_tmpStudentName,_tmpStudentNumber,_tmpTappedAt,_tmpStatus,_tmpSynced,_tmpCourseName,_tmpCourseCode);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDistinctSessionIds(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT session_id FROM attendance_records ORDER BY tapped_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object markSynced(final List<String> ids, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE attendance_records SET synced = 1 WHERE local_id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : ids) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
