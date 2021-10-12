package com.ldt.musicr.ui.page.librarypage.artist;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ldt.musicr.R;
import com.ldt.musicr.contract.AbsBindAbleHolder;
import com.ldt.musicr.contract.AbsMediaAdapter;
import com.ldt.musicr.glide.ArtistGlideRequest;
import com.ldt.musicr.helper.menu.MenuHelper;
import com.ldt.musicr.loader.medialoader.GenreLoader;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.model.Genre;
import com.ldt.musicr.ui.bottomsheet.OptionBottomSheet;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ArtistAdapter extends AbsMediaAdapter<AbsBindAbleHolder, Artist> implements FastScrollRecyclerView.SectionedAdapter {
    private static final String TAG = "ArtistAdapter";

    private static final int UN_SET = 0;
    private static final int AVAILABLE = 1;
    private static final int RUNNING = 2;

    private ArrayList<Genre>[] mGenres;
    private final HashMap<Artist, GenreArtistTask> mGenreArtistTaskMap = new HashMap<>();

    public interface ArtistClickListener {
        void onArtistItemClick(Artist artist);
    }

    private ArtistClickListener mListener;

    public void setArtistClickListener(ArtistClickListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    @Override
    protected void onMenuItemClick(int positionInData) {
    }

    @Override
    protected void onDataSet() {
        mGenres = new ArrayList[getData().size()];
    }

    private void clearAndCancelAllTask() {
        for (Map.Entry<Artist, GenreArtistTask> map :
                mGenreArtistTaskMap.entrySet()) {
            GenreArtistTask task = map.getValue();
            if (task != null) task.cancel();
        }

        mGenreArtistTaskMap.clear();
    }

    private void onTaskComplete(Artist artist, ArrayList<Genre> genres, int positionSaved) {
        mGenreArtistTaskMap.remove(artist);
        attachGenreByPosition(genres, artist, positionSaved);
    }

    private void attachGenreByPosition(ArrayList<Genre> genres, Artist artist, int itemPos) {
        if (itemPos >= 0 && itemPos < getData().size()) {
            if (artist.equals(getData().get(itemPos)) && mGenres[itemPos] == null) {
                mGenres[itemPos] = genres;
                notifyItemChanged(itemPos, GENRE_UPDATE);
            }
        }
    }

    @NonNull
    @Override
    public AbsBindAbleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_child, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AbsBindAbleHolder itemHolder, int i) {
        if (itemHolder instanceof ItemHolder) {
            if (mGenres != null && mGenres.length > i - 1)
                ((ItemHolder) itemHolder).bind(getData().get(i), mGenres[i]);
            else ((ItemHolder) itemHolder).bind(getData().get(i), null);
        }
    }

    @Override
    protected boolean onLongPressedItem(AbsBindAbleHolder holder, int position) {
        super.onLongPressedItem(holder, position);
        OptionBottomSheet.newInstance(MenuHelper.ARTIST_OPTION, getData().get(position)).show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "artist_option");
        return true;
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public static int lighter(int color, float factor, int alpha) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(alpha, red, green, blue);
    }

    @NonNull
    @Override
    public String getSectionName(int i) {
        if (getData().get(getDataPosition(i)).getName().isEmpty())
            return "";
        return getData().get(getDataPosition(i)).getName().substring(0, 1);
    }

    private static class GenreArtistTask extends AsyncTask<Void, Void, ArrayList<Genre>> {
        private final WeakReference<ArtistAdapter> mAAReference;
        private final int mItemPos;
        private final Artist mArtist;
        private boolean mCancelled = false;

        public GenreArtistTask(ArtistAdapter adapter, Artist artist, int itemPos) {
            super();
            mAAReference = new WeakReference<>(adapter);
            mArtist = artist;
            mItemPos = itemPos;
        }

        private void cancel() {
            mCancelled = true;
            cancel(true);
            mAAReference.clear();

        }

        @Override
        protected ArrayList<Genre> doInBackground(Void... voids) {

            if (mAAReference.get() != null && mArtist != null && !mCancelled) {
                return GenreLoader.getGenreForArtist(mAAReference.get().getContext(), mArtist.getId());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Genre> genres) {
            if (genres != null && mAAReference.get() != null && !mCancelled) {
                mAAReference.get().onTaskComplete(mArtist, genres, mItemPos);
            }
        }
    }

    class ItemHolder extends AbsMediaHolder {

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             /*   mImage.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, view.getWidth(), view.getHeight());
                    }
                });*/
                mImage.setClipToOutline(true);
            }
        }

        @BindView(R.id.title)
        TextView mArtist;

        @BindView(R.id.image)
        ImageView mImage;

        @BindView(R.id.genre_one)
        TextView mGenreOne;

        @BindView(R.id.genre_two)
        TextView mGenreTwo;

        @BindView(R.id.panel)
        View mPanel;
        @BindView(R.id.panel_color)
        View mPanelColor;

        @OnLongClick(R.id.panel)
        boolean onLongClickPanel() {
            return onLongPressedItem(this, getAdapterPosition());
        }

        @OnClick(R.id.panel)
        void goToThisArtist() {
            if (mListener != null) mListener.onArtistItemClick(getData().get(getAdapterPosition()));
        }

        @BindView(R.id.root)
        View mRoot;

        @BindView(R.id.count)
        TextView mCount;

        public void bind(Artist artist, ArrayList<Genre> genres) {
            mArtist.setText(artist.getName());
            mCount.setText(String.format("%d %s", artist.getSongCount(), mCount.getContext().getResources().getString(R.string.songs)));
            if (genres == null) {
                mGenreOne.setText("⋯");
                mGenreTwo.setVisibility(View.GONE);
                Log.d(TAG, "load genre item " + getAdapterPosition());

                GenreArtistTask task = mGenreArtistTaskMap.get(artist);
                if (task == null) {
                    task = new GenreArtistTask(ArtistAdapter.this, artist, getAdapterPosition());
                    mGenreArtistTaskMap.put(artist, task);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            } else bindGenre(genres);
            //try {
            loadArtistImage(artist);
            // } catch (Exception ignored) {}
        }

        private void loadArtistImage(Artist artist) {
          /* String[] artists = artist.getName().replace(" ft ",",").replace(';',',').split("\\s*,\\s*");
           Artist artistTemp;
           if(artists.length>1)
               artistTemp = new Artist(artist.id,artists[0].replace(",",""),artist.albumCount,artist.songCount);
           else artistTemp = artist;*/

            ArtistGlideRequest.Builder.from(Glide.with(getContext()), artist)
                    // .tryToLoadOriginal(true)
                    .generateBuilder(getContext())
                    .build()
                    .error(R.drawable.ic_artist)
                    .placeholder(R.drawable.ic_artist)
                    .into(mImage);
        }

        public void bindGenre(ArrayList<Genre> genres) {
            if (genres.isEmpty()) {
                mGenreOne.setText(R.string.unknown_genres);
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.GONE);
            } else if (genres.size() == 1) {
                mGenreOne.setText(genres.get(0).name);
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.GONE);
            } else {
                mGenreOne.setText(genres.get(0).name);
                mGenreTwo.setText(genres.get(0).name);
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbsBindAbleHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && holder instanceof ItemHolder) {
            if ((payloads.get(0)).equals(GENRE_UPDATE) && position < mGenres.length)
                ((ItemHolder) holder).bindGenre(mGenres[position]);
        } else
            super.onBindViewHolder(holder, position, payloads);
    }

    private static final String GENRE_UPDATE = "genre_update";


}
