package com.example.ourproject.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.ourproject.data.entity.ForecastEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class ForecastDao_Impl implements ForecastDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ForecastEntity> __insertionAdapterOfForecastEntity;

  private final EntityDeletionOrUpdateAdapter<ForecastEntity> __deletionAdapterOfForecastEntity;

  private final EntityDeletionOrUpdateAdapter<ForecastEntity> __updateAdapterOfForecastEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllForecasts;

  public ForecastDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfForecastEntity = new EntityInsertionAdapter<ForecastEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `forecasts` (`id`,`day`,`date`,`highTemp`,`lowTemp`,`precipitation`,`iconName`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ForecastEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDay());
        statement.bindString(3, entity.getDate());
        statement.bindLong(4, entity.getHighTemp());
        statement.bindLong(5, entity.getLowTemp());
        statement.bindLong(6, entity.getPrecipitation());
        statement.bindString(7, entity.getIconName());
      }
    };
    this.__deletionAdapterOfForecastEntity = new EntityDeletionOrUpdateAdapter<ForecastEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `forecasts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ForecastEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfForecastEntity = new EntityDeletionOrUpdateAdapter<ForecastEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `forecasts` SET `id` = ?,`day` = ?,`date` = ?,`highTemp` = ?,`lowTemp` = ?,`precipitation` = ?,`iconName` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ForecastEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDay());
        statement.bindString(3, entity.getDate());
        statement.bindLong(4, entity.getHighTemp());
        statement.bindLong(5, entity.getLowTemp());
        statement.bindLong(6, entity.getPrecipitation());
        statement.bindString(7, entity.getIconName());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllForecasts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM forecasts";
        return _query;
      }
    };
  }

  @Override
  public Object insertForecast(final ForecastEntity forecast,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfForecastEntity.insertAndReturnId(forecast);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllForecasts(final List<ForecastEntity> forecasts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfForecastEntity.insert(forecasts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForecast(final ForecastEntity forecast,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfForecastEntity.handle(forecast);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateForecast(final ForecastEntity forecast,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfForecastEntity.handle(forecast);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllForecasts(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllForecasts.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllForecasts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ForecastEntity>> getAllForecasts() {
    final String _sql = "SELECT * FROM forecasts ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"forecasts"}, new Callable<List<ForecastEntity>>() {
      @Override
      @NonNull
      public List<ForecastEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfHighTemp = CursorUtil.getColumnIndexOrThrow(_cursor, "highTemp");
          final int _cursorIndexOfLowTemp = CursorUtil.getColumnIndexOrThrow(_cursor, "lowTemp");
          final int _cursorIndexOfPrecipitation = CursorUtil.getColumnIndexOrThrow(_cursor, "precipitation");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "iconName");
          final List<ForecastEntity> _result = new ArrayList<ForecastEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ForecastEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDay;
            _tmpDay = _cursor.getString(_cursorIndexOfDay);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpHighTemp;
            _tmpHighTemp = _cursor.getInt(_cursorIndexOfHighTemp);
            final int _tmpLowTemp;
            _tmpLowTemp = _cursor.getInt(_cursorIndexOfLowTemp);
            final int _tmpPrecipitation;
            _tmpPrecipitation = _cursor.getInt(_cursorIndexOfPrecipitation);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            _item = new ForecastEntity(_tmpId,_tmpDay,_tmpDate,_tmpHighTemp,_tmpLowTemp,_tmpPrecipitation,_tmpIconName);
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
  public Object getForecastById(final long id,
      final Continuation<? super ForecastEntity> $completion) {
    final String _sql = "SELECT * FROM forecasts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ForecastEntity>() {
      @Override
      @Nullable
      public ForecastEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfHighTemp = CursorUtil.getColumnIndexOrThrow(_cursor, "highTemp");
          final int _cursorIndexOfLowTemp = CursorUtil.getColumnIndexOrThrow(_cursor, "lowTemp");
          final int _cursorIndexOfPrecipitation = CursorUtil.getColumnIndexOrThrow(_cursor, "precipitation");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "iconName");
          final ForecastEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDay;
            _tmpDay = _cursor.getString(_cursorIndexOfDay);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpHighTemp;
            _tmpHighTemp = _cursor.getInt(_cursorIndexOfHighTemp);
            final int _tmpLowTemp;
            _tmpLowTemp = _cursor.getInt(_cursorIndexOfLowTemp);
            final int _tmpPrecipitation;
            _tmpPrecipitation = _cursor.getInt(_cursorIndexOfPrecipitation);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            _result = new ForecastEntity(_tmpId,_tmpDay,_tmpDate,_tmpHighTemp,_tmpLowTemp,_tmpPrecipitation,_tmpIconName);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
