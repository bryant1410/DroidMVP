/*
 * Copyright (C) 2016 Appflate.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.appflate.droidmvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class DroidMVPFragment<M, V extends DroidMVPView, P extends DroidMVPPresenter<V, M>>
    extends Fragment implements DroidMVPView {

    private DroidMVPViewDelegate<M, V, P> mvpDelegate =
        new DroidMVPViewDelegate<M, V, P>(createPresentationModelSerializer()) {
            @NonNull @Override protected P createPresenter() {
                return DroidMVPFragment.this.createPresenter();
            }

            @NonNull @Override protected M createPresentationModel() {
                return DroidMVPFragment.this.createPresentationModel();
            }
        };

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performFieldInjection();
        mvpDelegate.onCreate(this, savedInstanceState);
    }

    @Override public void onStart() {
        super.onStart();
        mvpDelegate.onStart((V) this);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mvpDelegate.onSaveInstanceState(outState);
    }

    @Override public void onStop() {
        super.onStop();
        mvpDelegate.onStop();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mvpDelegate.onDestroy();
    }

    /**
     * Feel free to override this method that returns your own implementation of
     * PresentationModelSerializer.
     * Useful if you use a Parceler library for example
     *
     * @return an instance of PresentationModelSerializer that will serialize and deserialize your
     * PresentationModel from Bundle.
     */
    protected PresentationModelSerializer<M> createPresentationModelSerializer() {
        return new ParcelableAndSerializablePresentationModelSerializer<>();
    }

    @NonNull protected P getPresenter() {
        return mvpDelegate.getPresenter();
    }

    /**
     * Used for performing field injection trough various dependency injection frameworks like
     * Dagger. The injection is performed just before the #createPresenter() or
     * #createPresentationModel() methods are called, so you can
     * have your presenter and/or Presentation Model being injected by Dagger.
     */
    protected abstract void performFieldInjection();

    /**
     * Used for creating the presenter instance, called in #onCreate(Bundle) method.
     *
     * @return an instance of your Presenter.
     */
    @NonNull protected abstract P createPresenter();

    /**
     * Used to create the Presentation Model that will be attached to your presenter in #onAttach()
     * method of your presenter.
     *
     * NOTE: this will be called only if there is no Presentation Model persisted in your
     * savedInstanceState!
     *
     * You can retrieve the arguments from #getArguments() method of your
     * fragment and pass it to your Presentation's model constructor.
     *
     * @return Presentation Model instance used by your Presenter.
     */
    @NonNull protected abstract M createPresentationModel();
}