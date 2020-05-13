package com.ytrewqwert.yetanotherjnovelreader.data.local.database;

import android.database.Cursor;
import androidx.collection.ArrayMap;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;

import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow;
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part;
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull;
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress;
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie;
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull;
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume;
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull;

import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class PartDao_Impl implements PartDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Serie> __insertionAdapterOfSerie;

  private final EntityInsertionAdapter<Volume> __insertionAdapterOfVolume;

  private final EntityInsertionAdapter<Part> __insertionAdapterOfPart;

  private final EntityInsertionAdapter<Progress> __insertionAdapterOfProgress;

  private final EntityInsertionAdapter<Follow> __insertionAdapterOfFollow;

  private final EntityDeletionOrUpdateAdapter<Follow> __deletionAdapterOfFollow;

  private final EntityDeletionOrUpdateAdapter<Serie> __updateAdapterOfSerie;

  private final EntityDeletionOrUpdateAdapter<Volume> __updateAdapterOfVolume;

  private final EntityDeletionOrUpdateAdapter<Part> __updateAdapterOfPart;

  private final EntityDeletionOrUpdateAdapter<Progress> __updateAdapterOfProgress;

  public PartDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSerie = new EntityInsertionAdapter<Serie>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `Serie` (`id`,`title`,`titleslug`,`description`,`descriptionShort`,`coverUrl`,`tags`,`created`,`overrideExpiration`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Serie value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTitle());
        }
        if (value.getTitleslug() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitleslug());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        if (value.getDescriptionShort() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getDescriptionShort());
        }
        if (value.getCoverUrl() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getCoverUrl());
        }
        if (value.getTags() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getTags());
        }
        if (value.getCreated() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getCreated());
        }
        final int _tmp;
        _tmp = value.getOverrideExpiration() ? 1 : 0;
        stmt.bindLong(9, _tmp);
      }
    };
    this.__insertionAdapterOfVolume = new EntityInsertionAdapter<Volume>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `Volume` (`id`,`serieId`,`title`,`titleslug`,`volumeNum`,`description`,`descriptionShort`,`coverUrl`,`tags`,`created`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Volume value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getSerieId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getSerieId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitle());
        }
        if (value.getTitleslug() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTitleslug());
        }
        stmt.bindLong(5, value.getVolumeNum());
        if (value.getDescription() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getDescription());
        }
        if (value.getDescriptionShort() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDescriptionShort());
        }
        if (value.getCoverUrl() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getCoverUrl());
        }
        if (value.getTags() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getTags());
        }
        if (value.getCreated() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getCreated());
        }
      }
    };
    this.__insertionAdapterOfPart = new EntityInsertionAdapter<Part>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `Part` (`id`,`volumeId`,`serieId`,`title`,`titleslug`,`seriesPartNum`,`coverUrl`,`tags`,`launchDate`,`expired`,`preview`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Part value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getVolumeId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getVolumeId());
        }
        if (value.getSerieId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getSerieId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTitle());
        }
        if (value.getTitleslug() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getTitleslug());
        }
        stmt.bindLong(6, value.getSeriesPartNum());
        if (value.getCoverUrl() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getCoverUrl());
        }
        if (value.getTags() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getTags());
        }
        if (value.getLaunchDate() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getLaunchDate());
        }
        final int _tmp;
        _tmp = value.getExpired() ? 1 : 0;
        stmt.bindLong(10, _tmp);
        final int _tmp_1;
        _tmp_1 = value.getPreview() ? 1 : 0;
        stmt.bindLong(11, _tmp_1);
      }
    };
    this.__insertionAdapterOfProgress = new EntityInsertionAdapter<Progress>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `Progress` (`partId`,`progress`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Progress value) {
        if (value.getPartId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getPartId());
        }
        stmt.bindDouble(2, value.getProgress());
      }
    };
    this.__insertionAdapterOfFollow = new EntityInsertionAdapter<Follow>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `Follow` (`serieId`) VALUES (?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Follow value) {
        if (value.getSerieId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getSerieId());
        }
      }
    };
    this.__deletionAdapterOfFollow = new EntityDeletionOrUpdateAdapter<Follow>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `Follow` WHERE `serieId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Follow value) {
        if (value.getSerieId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getSerieId());
        }
      }
    };
    this.__updateAdapterOfSerie = new EntityDeletionOrUpdateAdapter<Serie>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `Serie` SET `id` = ?,`title` = ?,`titleslug` = ?,`description` = ?,`descriptionShort` = ?,`coverUrl` = ?,`tags` = ?,`created` = ?,`overrideExpiration` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Serie value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTitle());
        }
        if (value.getTitleslug() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitleslug());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        if (value.getDescriptionShort() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getDescriptionShort());
        }
        if (value.getCoverUrl() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getCoverUrl());
        }
        if (value.getTags() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getTags());
        }
        if (value.getCreated() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getCreated());
        }
        final int _tmp;
        _tmp = value.getOverrideExpiration() ? 1 : 0;
        stmt.bindLong(9, _tmp);
        if (value.getId() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getId());
        }
      }
    };
    this.__updateAdapterOfVolume = new EntityDeletionOrUpdateAdapter<Volume>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `Volume` SET `id` = ?,`serieId` = ?,`title` = ?,`titleslug` = ?,`volumeNum` = ?,`description` = ?,`descriptionShort` = ?,`coverUrl` = ?,`tags` = ?,`created` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Volume value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getSerieId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getSerieId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitle());
        }
        if (value.getTitleslug() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTitleslug());
        }
        stmt.bindLong(5, value.getVolumeNum());
        if (value.getDescription() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getDescription());
        }
        if (value.getDescriptionShort() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDescriptionShort());
        }
        if (value.getCoverUrl() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getCoverUrl());
        }
        if (value.getTags() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getTags());
        }
        if (value.getCreated() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getCreated());
        }
        if (value.getId() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getId());
        }
      }
    };
    this.__updateAdapterOfPart = new EntityDeletionOrUpdateAdapter<Part>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `Part` SET `id` = ?,`volumeId` = ?,`serieId` = ?,`title` = ?,`titleslug` = ?,`seriesPartNum` = ?,`coverUrl` = ?,`tags` = ?,`launchDate` = ?,`expired` = ?,`preview` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Part value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getVolumeId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getVolumeId());
        }
        if (value.getSerieId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getSerieId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTitle());
        }
        if (value.getTitleslug() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getTitleslug());
        }
        stmt.bindLong(6, value.getSeriesPartNum());
        if (value.getCoverUrl() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getCoverUrl());
        }
        if (value.getTags() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getTags());
        }
        if (value.getLaunchDate() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getLaunchDate());
        }
        final int _tmp;
        _tmp = value.getExpired() ? 1 : 0;
        stmt.bindLong(10, _tmp);
        final int _tmp_1;
        _tmp_1 = value.getPreview() ? 1 : 0;
        stmt.bindLong(11, _tmp_1);
        if (value.getId() == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindString(12, value.getId());
        }
      }
    };
    this.__updateAdapterOfProgress = new EntityDeletionOrUpdateAdapter<Progress>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `Progress` SET `partId` = ?,`progress` = ? WHERE `partId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Progress value) {
        if (value.getPartId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getPartId());
        }
        stmt.bindDouble(2, value.getProgress());
        if (value.getPartId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPartId());
        }
      }
    };
  }

  @Override
  public Object insertSeries(final Serie[] series, final Continuation<? super long[]> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<long[]>() {
      @Override
      public long[] call() throws Exception {
        __db.beginTransaction();
        try {
          long[] _result = __insertionAdapterOfSerie.insertAndReturnIdsArray(series);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object insertVolumes(final Volume[] volumes, final Continuation<? super long[]> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<long[]>() {
      @Override
      public long[] call() throws Exception {
        __db.beginTransaction();
        try {
          long[] _result = __insertionAdapterOfVolume.insertAndReturnIdsArray(volumes);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object insertParts(final Part[] parts, final Continuation<? super long[]> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<long[]>() {
      @Override
      public long[] call() throws Exception {
        __db.beginTransaction();
        try {
          long[] _result = __insertionAdapterOfPart.insertAndReturnIdsArray(parts);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object insertProgress(final Progress[] progress, final Continuation<? super long[]> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<long[]>() {
      @Override
      public long[] call() throws Exception {
        __db.beginTransaction();
        try {
          long[] _result = __insertionAdapterOfProgress.insertAndReturnIdsArray(progress);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object insertFollows(final Follow[] follows, final Continuation<? super long[]> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<long[]>() {
      @Override
      public long[] call() throws Exception {
        __db.beginTransaction();
        try {
          long[] _result = __insertionAdapterOfFollow.insertAndReturnIdsArray(follows);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object deleteFollows(final Follow[] follows, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFollow.handleMultiple(follows);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object updateSeries(final Serie[] series, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSerie.handleMultiple(series);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object updateVolumes(final Volume[] volumes, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVolume.handleMultiple(volumes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object updateParts(final Part[] parts, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPart.handleMultiple(parts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object updateProgress(final Progress[] progress, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfProgress.handleMultiple(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object upsertSeries(final Serie[] series, final Continuation<? super Unit> p1) {
    return RoomDatabaseKt.withTransaction(__db, new Function1<Continuation<? super Unit>, Object>() {
      @Override
      public Object invoke(Continuation<? super Unit> __cont) {
        return PartDao.DefaultImpls.upsertSeries(PartDao_Impl.this, series, __cont);
      }
    }, p1);
  }

  @Override
  public Object upsertVolumes(final Volume[] volumes, final Continuation<? super Unit> p1) {
    return RoomDatabaseKt.withTransaction(__db, new Function1<Continuation<? super Unit>, Object>() {
      @Override
      public Object invoke(Continuation<? super Unit> __cont) {
        return PartDao.DefaultImpls.upsertVolumes(PartDao_Impl.this, volumes, __cont);
      }
    }, p1);
  }

  @Override
  public Object upsertParts(final Part[] parts, final Continuation<? super Unit> p1) {
    return RoomDatabaseKt.withTransaction(__db, new Function1<Continuation<? super Unit>, Object>() {
      @Override
      public Object invoke(Continuation<? super Unit> __cont) {
        return PartDao.DefaultImpls.upsertParts(PartDao_Impl.this, parts, __cont);
      }
    }, p1);
  }

  @Override
  public Object upsertProgress(final Progress[] progress, final Continuation<? super Unit> p1) {
    return RoomDatabaseKt.withTransaction(__db, new Function1<Continuation<? super Unit>, Object>() {
      @Override
      public Object invoke(Continuation<? super Unit> __cont) {
        return PartDao.DefaultImpls.upsertProgress(PartDao_Impl.this, progress, __cont);
      }
    }, p1);
  }

  @Override
  public Flow<List<SerieFull>> getAllSeries() {
    final String _sql = "SELECT * FROM Serie";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[]{"Follow","Serie"}, new Callable<List<SerieFull>>() {
      @Override
      public List<SerieFull> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfTitleslug = CursorUtil.getColumnIndexOrThrow(_cursor, "titleslug");
            final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
            final int _cursorIndexOfDescriptionShort = CursorUtil.getColumnIndexOrThrow(_cursor, "descriptionShort");
            final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
            final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
            final int _cursorIndexOfCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "created");
            final int _cursorIndexOfOverrideExpiration = CursorUtil.getColumnIndexOrThrow(_cursor, "overrideExpiration");
            final ArrayMap<String, Follow> _collectionFollowing = new ArrayMap<String, Follow>();
            while (_cursor.moveToNext()) {
              final String _tmpKey = _cursor.getString(_cursorIndexOfId);
              _collectionFollowing.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(_collectionFollowing);
            final List<SerieFull> _result = new ArrayList<SerieFull>(_cursor.getCount());
            while(_cursor.moveToNext()) {
              final SerieFull _item;
              final Serie _tmpSerie;
              if (! (_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfTitle) && _cursor.isNull(_cursorIndexOfTitleslug) && _cursor.isNull(_cursorIndexOfDescription) && _cursor.isNull(_cursorIndexOfDescriptionShort) && _cursor.isNull(_cursorIndexOfCoverUrl) && _cursor.isNull(_cursorIndexOfTags) && _cursor.isNull(_cursorIndexOfCreated) && _cursor.isNull(_cursorIndexOfOverrideExpiration))) {
                final String _tmpId;
                _tmpId = _cursor.getString(_cursorIndexOfId);
                final String _tmpTitle;
                _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
                final String _tmpTitleslug;
                _tmpTitleslug = _cursor.getString(_cursorIndexOfTitleslug);
                final String _tmpDescription;
                _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
                final String _tmpDescriptionShort;
                _tmpDescriptionShort = _cursor.getString(_cursorIndexOfDescriptionShort);
                final String _tmpCoverUrl;
                _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
                final String _tmpTags;
                _tmpTags = _cursor.getString(_cursorIndexOfTags);
                final String _tmpCreated;
                _tmpCreated = _cursor.getString(_cursorIndexOfCreated);
                final boolean _tmpOverrideExpiration;
                final int _tmp;
                _tmp = _cursor.getInt(_cursorIndexOfOverrideExpiration);
                _tmpOverrideExpiration = _tmp != 0;
                _tmpSerie = new Serie(_tmpId,_tmpTitle,_tmpTitleslug,_tmpDescription,_tmpDescriptionShort,_tmpCoverUrl,_tmpTags,_tmpCreated,_tmpOverrideExpiration);
              }  else  {
                _tmpSerie = null;
              }
              Follow _tmpFollowing = null;
              final String _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              _tmpFollowing = _collectionFollowing.get(_tmpKey_1);
              _item = new SerieFull(_tmpSerie,_tmpFollowing);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VolumeFull>> getSerieVolumes(final String serieId) {
    final String _sql = "SELECT * FROM Volume WHERE serieId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (serieId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, serieId);
    }
    return CoroutinesRoom.createFlow(__db, true, new String[]{"Follow","Volume"}, new Callable<List<VolumeFull>>() {
      @Override
      public List<VolumeFull> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfSerieId = CursorUtil.getColumnIndexOrThrow(_cursor, "serieId");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfTitleslug = CursorUtil.getColumnIndexOrThrow(_cursor, "titleslug");
            final int _cursorIndexOfVolumeNum = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeNum");
            final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
            final int _cursorIndexOfDescriptionShort = CursorUtil.getColumnIndexOrThrow(_cursor, "descriptionShort");
            final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
            final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
            final int _cursorIndexOfCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "created");
            final ArrayMap<String, Follow> _collectionFollowing = new ArrayMap<String, Follow>();
            while (_cursor.moveToNext()) {
              final String _tmpKey = _cursor.getString(_cursorIndexOfSerieId);
              _collectionFollowing.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(_collectionFollowing);
            final List<VolumeFull> _result = new ArrayList<VolumeFull>(_cursor.getCount());
            while(_cursor.moveToNext()) {
              final VolumeFull _item;
              final Volume _tmpVolume;
              if (! (_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfSerieId) && _cursor.isNull(_cursorIndexOfTitle) && _cursor.isNull(_cursorIndexOfTitleslug) && _cursor.isNull(_cursorIndexOfVolumeNum) && _cursor.isNull(_cursorIndexOfDescription) && _cursor.isNull(_cursorIndexOfDescriptionShort) && _cursor.isNull(_cursorIndexOfCoverUrl) && _cursor.isNull(_cursorIndexOfTags) && _cursor.isNull(_cursorIndexOfCreated))) {
                final String _tmpId;
                _tmpId = _cursor.getString(_cursorIndexOfId);
                final String _tmpSerieId;
                _tmpSerieId = _cursor.getString(_cursorIndexOfSerieId);
                final String _tmpTitle;
                _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
                final String _tmpTitleslug;
                _tmpTitleslug = _cursor.getString(_cursorIndexOfTitleslug);
                final int _tmpVolumeNum;
                _tmpVolumeNum = _cursor.getInt(_cursorIndexOfVolumeNum);
                final String _tmpDescription;
                _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
                final String _tmpDescriptionShort;
                _tmpDescriptionShort = _cursor.getString(_cursorIndexOfDescriptionShort);
                final String _tmpCoverUrl;
                _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
                final String _tmpTags;
                _tmpTags = _cursor.getString(_cursorIndexOfTags);
                final String _tmpCreated;
                _tmpCreated = _cursor.getString(_cursorIndexOfCreated);
                _tmpVolume = new Volume(_tmpId,_tmpSerieId,_tmpTitle,_tmpTitleslug,_tmpVolumeNum,_tmpDescription,_tmpDescriptionShort,_tmpCoverUrl,_tmpTags,_tmpCreated);
              }  else  {
                _tmpVolume = null;
              }
              Follow _tmpFollowing = null;
              final String _tmpKey_1 = _cursor.getString(_cursorIndexOfSerieId);
              _tmpFollowing = _collectionFollowing.get(_tmpKey_1);
              _item = new VolumeFull(_tmpVolume,_tmpFollowing);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PartFull>> getVolumeParts(final String volumeId) {
    final String _sql = "SELECT * FROM Part WHERE volumeId = ? ORDER BY seriesPartNum ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (volumeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, volumeId);
    }
    return CoroutinesRoom.createFlow(__db, true, new String[]{"Progress","Follow","Part"}, new Callable<List<PartFull>>() {
      @Override
      public List<PartFull> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfVolumeId = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeId");
            final int _cursorIndexOfSerieId = CursorUtil.getColumnIndexOrThrow(_cursor, "serieId");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfTitleslug = CursorUtil.getColumnIndexOrThrow(_cursor, "titleslug");
            final int _cursorIndexOfSeriesPartNum = CursorUtil.getColumnIndexOrThrow(_cursor, "seriesPartNum");
            final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
            final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
            final int _cursorIndexOfLaunchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "launchDate");
            final int _cursorIndexOfExpired = CursorUtil.getColumnIndexOrThrow(_cursor, "expired");
            final int _cursorIndexOfPreview = CursorUtil.getColumnIndexOrThrow(_cursor, "preview");
            final ArrayMap<String, Progress> _collectionProgress = new ArrayMap<String, Progress>();
            final ArrayMap<String, Follow> _collectionFollowing = new ArrayMap<String, Follow>();
            while (_cursor.moveToNext()) {
              final String _tmpKey = _cursor.getString(_cursorIndexOfId);
              _collectionProgress.put(_tmpKey, null);
              final String _tmpKey_1 = _cursor.getString(_cursorIndexOfSerieId);
              _collectionFollowing.put(_tmpKey_1, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipProgressAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseProgress(_collectionProgress);
            __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(_collectionFollowing);
            final List<PartFull> _result = new ArrayList<PartFull>(_cursor.getCount());
            while(_cursor.moveToNext()) {
              final PartFull _item;
              final Part _tmpPart;
              if (! (_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfVolumeId) && _cursor.isNull(_cursorIndexOfSerieId) && _cursor.isNull(_cursorIndexOfTitle) && _cursor.isNull(_cursorIndexOfTitleslug) && _cursor.isNull(_cursorIndexOfSeriesPartNum) && _cursor.isNull(_cursorIndexOfCoverUrl) && _cursor.isNull(_cursorIndexOfTags) && _cursor.isNull(_cursorIndexOfLaunchDate) && _cursor.isNull(_cursorIndexOfExpired) && _cursor.isNull(_cursorIndexOfPreview))) {
                final String _tmpId;
                _tmpId = _cursor.getString(_cursorIndexOfId);
                final String _tmpVolumeId;
                _tmpVolumeId = _cursor.getString(_cursorIndexOfVolumeId);
                final String _tmpSerieId;
                _tmpSerieId = _cursor.getString(_cursorIndexOfSerieId);
                final String _tmpTitle;
                _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
                final String _tmpTitleslug;
                _tmpTitleslug = _cursor.getString(_cursorIndexOfTitleslug);
                final int _tmpSeriesPartNum;
                _tmpSeriesPartNum = _cursor.getInt(_cursorIndexOfSeriesPartNum);
                final String _tmpCoverUrl;
                _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
                final String _tmpTags;
                _tmpTags = _cursor.getString(_cursorIndexOfTags);
                final String _tmpLaunchDate;
                _tmpLaunchDate = _cursor.getString(_cursorIndexOfLaunchDate);
                final boolean _tmpExpired;
                final int _tmp;
                _tmp = _cursor.getInt(_cursorIndexOfExpired);
                _tmpExpired = _tmp != 0;
                final boolean _tmpPreview;
                final int _tmp_1;
                _tmp_1 = _cursor.getInt(_cursorIndexOfPreview);
                _tmpPreview = _tmp_1 != 0;
                _tmpPart = new Part(_tmpId,_tmpVolumeId,_tmpSerieId,_tmpTitle,_tmpTitleslug,_tmpSeriesPartNum,_tmpCoverUrl,_tmpTags,_tmpLaunchDate,_tmpExpired,_tmpPreview);
              }  else  {
                _tmpPart = null;
              }
              Progress _tmpProgress = null;
              final String _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpProgress = _collectionProgress.get(_tmpKey_2);
              Follow _tmpFollowing = null;
              final String _tmpKey_3 = _cursor.getString(_cursorIndexOfSerieId);
              _tmpFollowing = _collectionFollowing.get(_tmpKey_3);
              _item = new PartFull(_tmpPart,_tmpProgress,_tmpFollowing);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PartFull>> getPartsSince(final String time) {
    final String _sql = "SELECT * FROM Part WHERE launchDate >= ? ORDER BY launchDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (time == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, time);
    }
    return CoroutinesRoom.createFlow(__db, true, new String[]{"Progress","Follow","Part"}, new Callable<List<PartFull>>() {
      @Override
      public List<PartFull> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfVolumeId = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeId");
            final int _cursorIndexOfSerieId = CursorUtil.getColumnIndexOrThrow(_cursor, "serieId");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfTitleslug = CursorUtil.getColumnIndexOrThrow(_cursor, "titleslug");
            final int _cursorIndexOfSeriesPartNum = CursorUtil.getColumnIndexOrThrow(_cursor, "seriesPartNum");
            final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
            final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
            final int _cursorIndexOfLaunchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "launchDate");
            final int _cursorIndexOfExpired = CursorUtil.getColumnIndexOrThrow(_cursor, "expired");
            final int _cursorIndexOfPreview = CursorUtil.getColumnIndexOrThrow(_cursor, "preview");
            final ArrayMap<String, Progress> _collectionProgress = new ArrayMap<String, Progress>();
            final ArrayMap<String, Follow> _collectionFollowing = new ArrayMap<String, Follow>();
            while (_cursor.moveToNext()) {
              final String _tmpKey = _cursor.getString(_cursorIndexOfId);
              _collectionProgress.put(_tmpKey, null);
              final String _tmpKey_1 = _cursor.getString(_cursorIndexOfSerieId);
              _collectionFollowing.put(_tmpKey_1, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipProgressAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseProgress(_collectionProgress);
            __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(_collectionFollowing);
            final List<PartFull> _result = new ArrayList<PartFull>(_cursor.getCount());
            while(_cursor.moveToNext()) {
              final PartFull _item;
              final Part _tmpPart;
              if (! (_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfVolumeId) && _cursor.isNull(_cursorIndexOfSerieId) && _cursor.isNull(_cursorIndexOfTitle) && _cursor.isNull(_cursorIndexOfTitleslug) && _cursor.isNull(_cursorIndexOfSeriesPartNum) && _cursor.isNull(_cursorIndexOfCoverUrl) && _cursor.isNull(_cursorIndexOfTags) && _cursor.isNull(_cursorIndexOfLaunchDate) && _cursor.isNull(_cursorIndexOfExpired) && _cursor.isNull(_cursorIndexOfPreview))) {
                final String _tmpId;
                _tmpId = _cursor.getString(_cursorIndexOfId);
                final String _tmpVolumeId;
                _tmpVolumeId = _cursor.getString(_cursorIndexOfVolumeId);
                final String _tmpSerieId;
                _tmpSerieId = _cursor.getString(_cursorIndexOfSerieId);
                final String _tmpTitle;
                _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
                final String _tmpTitleslug;
                _tmpTitleslug = _cursor.getString(_cursorIndexOfTitleslug);
                final int _tmpSeriesPartNum;
                _tmpSeriesPartNum = _cursor.getInt(_cursorIndexOfSeriesPartNum);
                final String _tmpCoverUrl;
                _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
                final String _tmpTags;
                _tmpTags = _cursor.getString(_cursorIndexOfTags);
                final String _tmpLaunchDate;
                _tmpLaunchDate = _cursor.getString(_cursorIndexOfLaunchDate);
                final boolean _tmpExpired;
                final int _tmp;
                _tmp = _cursor.getInt(_cursorIndexOfExpired);
                _tmpExpired = _tmp != 0;
                final boolean _tmpPreview;
                final int _tmp_1;
                _tmp_1 = _cursor.getInt(_cursorIndexOfPreview);
                _tmpPreview = _tmp_1 != 0;
                _tmpPart = new Part(_tmpId,_tmpVolumeId,_tmpSerieId,_tmpTitle,_tmpTitleslug,_tmpSeriesPartNum,_tmpCoverUrl,_tmpTags,_tmpLaunchDate,_tmpExpired,_tmpPreview);
              }  else  {
                _tmpPart = null;
              }
              Progress _tmpProgress = null;
              final String _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpProgress = _collectionProgress.get(_tmpKey_2);
              Follow _tmpFollowing = null;
              final String _tmpKey_3 = _cursor.getString(_cursorIndexOfSerieId);
              _tmpFollowing = _collectionFollowing.get(_tmpKey_3);
              _item = new PartFull(_tmpPart,_tmpProgress,_tmpFollowing);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getParts(final String[] partId, final Continuation<? super List<PartFull>> p1) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT ");
    _stringBuilder.append("*");
    _stringBuilder.append(" FROM Part WHERE id = ");
    final int _inputSize = partId.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : partId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    return CoroutinesRoom.execute(__db, true, new Callable<List<PartFull>>() {
      @Override
      public List<PartFull> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfVolumeId = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeId");
            final int _cursorIndexOfSerieId = CursorUtil.getColumnIndexOrThrow(_cursor, "serieId");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfTitleslug = CursorUtil.getColumnIndexOrThrow(_cursor, "titleslug");
            final int _cursorIndexOfSeriesPartNum = CursorUtil.getColumnIndexOrThrow(_cursor, "seriesPartNum");
            final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
            final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
            final int _cursorIndexOfLaunchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "launchDate");
            final int _cursorIndexOfExpired = CursorUtil.getColumnIndexOrThrow(_cursor, "expired");
            final int _cursorIndexOfPreview = CursorUtil.getColumnIndexOrThrow(_cursor, "preview");
            final ArrayMap<String, Progress> _collectionProgress = new ArrayMap<String, Progress>();
            final ArrayMap<String, Follow> _collectionFollowing = new ArrayMap<String, Follow>();
            while (_cursor.moveToNext()) {
              final String _tmpKey = _cursor.getString(_cursorIndexOfId);
              _collectionProgress.put(_tmpKey, null);
              final String _tmpKey_1 = _cursor.getString(_cursorIndexOfSerieId);
              _collectionFollowing.put(_tmpKey_1, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipProgressAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseProgress(_collectionProgress);
            __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(_collectionFollowing);
            final List<PartFull> _result = new ArrayList<PartFull>(_cursor.getCount());
            while(_cursor.moveToNext()) {
              final PartFull _item_1;
              final Part _tmpPart;
              if (! (_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfVolumeId) && _cursor.isNull(_cursorIndexOfSerieId) && _cursor.isNull(_cursorIndexOfTitle) && _cursor.isNull(_cursorIndexOfTitleslug) && _cursor.isNull(_cursorIndexOfSeriesPartNum) && _cursor.isNull(_cursorIndexOfCoverUrl) && _cursor.isNull(_cursorIndexOfTags) && _cursor.isNull(_cursorIndexOfLaunchDate) && _cursor.isNull(_cursorIndexOfExpired) && _cursor.isNull(_cursorIndexOfPreview))) {
                final String _tmpId;
                _tmpId = _cursor.getString(_cursorIndexOfId);
                final String _tmpVolumeId;
                _tmpVolumeId = _cursor.getString(_cursorIndexOfVolumeId);
                final String _tmpSerieId;
                _tmpSerieId = _cursor.getString(_cursorIndexOfSerieId);
                final String _tmpTitle;
                _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
                final String _tmpTitleslug;
                _tmpTitleslug = _cursor.getString(_cursorIndexOfTitleslug);
                final int _tmpSeriesPartNum;
                _tmpSeriesPartNum = _cursor.getInt(_cursorIndexOfSeriesPartNum);
                final String _tmpCoverUrl;
                _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
                final String _tmpTags;
                _tmpTags = _cursor.getString(_cursorIndexOfTags);
                final String _tmpLaunchDate;
                _tmpLaunchDate = _cursor.getString(_cursorIndexOfLaunchDate);
                final boolean _tmpExpired;
                final int _tmp;
                _tmp = _cursor.getInt(_cursorIndexOfExpired);
                _tmpExpired = _tmp != 0;
                final boolean _tmpPreview;
                final int _tmp_1;
                _tmp_1 = _cursor.getInt(_cursorIndexOfPreview);
                _tmpPreview = _tmp_1 != 0;
                _tmpPart = new Part(_tmpId,_tmpVolumeId,_tmpSerieId,_tmpTitle,_tmpTitleslug,_tmpSeriesPartNum,_tmpCoverUrl,_tmpTags,_tmpLaunchDate,_tmpExpired,_tmpPreview);
              }  else  {
                _tmpPart = null;
              }
              Progress _tmpProgress = null;
              final String _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpProgress = _collectionProgress.get(_tmpKey_2);
              Follow _tmpFollowing = null;
              final String _tmpKey_3 = _cursor.getString(_cursorIndexOfSerieId);
              _tmpFollowing = _collectionFollowing.get(_tmpKey_3);
              _item_1 = new PartFull(_tmpPart,_tmpProgress,_tmpFollowing);
              _result.add(_item_1);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  private void __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(
      final ArrayMap<String, Follow> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    // check if the size is too big, if so divide;
    if(_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      ArrayMap<String, Follow> _tmpInnerMap = new ArrayMap<String, Follow>(androidx.room.RoomDatabase.MAX_BIND_PARAMETER_CNT);
      int _tmpIndex = 0;
      int _mapIndex = 0;
      final int _limit = _map.size();
      while(_mapIndex < _limit) {
        _tmpInnerMap.put(_map.keyAt(_mapIndex), null);
        _mapIndex++;
        _tmpIndex++;
        if(_tmpIndex == RoomDatabase.MAX_BIND_PARAMETER_CNT) {
          __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(_tmpInnerMap);
          _map.putAll((Map<String, Follow>) _tmpInnerMap);
          _tmpInnerMap = new ArrayMap<String, Follow>(RoomDatabase.MAX_BIND_PARAMETER_CNT);
          _tmpIndex = 0;
        }
      }
      if(_tmpIndex > 0) {
        __fetchRelationshipFollowAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseFollow(_tmpInnerMap);
        _map.putAll((Map<String, Follow>) _tmpInnerMap);
      }
      return;
    }
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `serieId` FROM `Follow` WHERE `serieId` IN (");
    final int _inputSize = __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : __mapKeySet) {
      if (_item == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "serieId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfSerieId = CursorUtil.getColumnIndex(_cursor, "serieId");
      while(_cursor.moveToNext()) {
        final String _tmpKey = _cursor.getString(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final Follow _item_1;
          final String _tmpSerieId;
          if (_cursorIndexOfSerieId == -1) {
            _tmpSerieId = null;
          } else {
            _tmpSerieId = _cursor.getString(_cursorIndexOfSerieId);
          }
          _item_1 = new Follow(_tmpSerieId);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }

  private void __fetchRelationshipProgressAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseProgress(
      final ArrayMap<String, Progress> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    // check if the size is too big, if so divide;
    if(_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      ArrayMap<String, Progress> _tmpInnerMap = new ArrayMap<String, Progress>(androidx.room.RoomDatabase.MAX_BIND_PARAMETER_CNT);
      int _tmpIndex = 0;
      int _mapIndex = 0;
      final int _limit = _map.size();
      while(_mapIndex < _limit) {
        _tmpInnerMap.put(_map.keyAt(_mapIndex), null);
        _mapIndex++;
        _tmpIndex++;
        if(_tmpIndex == RoomDatabase.MAX_BIND_PARAMETER_CNT) {
          __fetchRelationshipProgressAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseProgress(_tmpInnerMap);
          _map.putAll((Map<String, Progress>) _tmpInnerMap);
          _tmpInnerMap = new ArrayMap<String, Progress>(RoomDatabase.MAX_BIND_PARAMETER_CNT);
          _tmpIndex = 0;
        }
      }
      if(_tmpIndex > 0) {
        __fetchRelationshipProgressAscomYtrewqwertYetanotherjnovelreaderDataLocalDatabaseProgress(_tmpInnerMap);
        _map.putAll((Map<String, Progress>) _tmpInnerMap);
      }
      return;
    }
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `partId`,`progress` FROM `Progress` WHERE `partId` IN (");
    final int _inputSize = __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : __mapKeySet) {
      if (_item == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "partId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfPartId = CursorUtil.getColumnIndex(_cursor, "partId");
      final int _cursorIndexOfProgress = CursorUtil.getColumnIndex(_cursor, "progress");
      while(_cursor.moveToNext()) {
        final String _tmpKey = _cursor.getString(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final Progress _item_1;
          final String _tmpPartId;
          if (_cursorIndexOfPartId == -1) {
            _tmpPartId = null;
          } else {
            _tmpPartId = _cursor.getString(_cursorIndexOfPartId);
          }
          final double _tmpProgress;
          if (_cursorIndexOfProgress == -1) {
            _tmpProgress = 0;
          } else {
            _tmpProgress = _cursor.getDouble(_cursorIndexOfProgress);
          }
          _item_1 = new Progress(_tmpPartId,_tmpProgress);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
