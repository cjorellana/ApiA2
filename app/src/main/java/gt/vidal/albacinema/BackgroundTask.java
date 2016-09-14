package gt.vidal.albacinema;

import android.os.AsyncTask;


/**
 * Created by alejandroalvarado on 9/04/15.
 */

public class BackgroundTask<V> extends AsyncTask<EstudiantesCallable<V>, Void, V>
{
    Exception exception;
    ParamRunnable<V>  doAfter;
    EstudiantesCallable<V> mTask;

    public BackgroundTask(EstudiantesCallable<V> task, ParamRunnable<V> doAfter)
    {
        this.mTask = task;
        this.doAfter = doAfter;
    }

    @Override
    protected  V doInBackground(EstudiantesCallable<V>... params)
    {
        EstudiantesCallable<V> task;

        task = params.length > 0 ? params[0] : null ;
        if (task == null)
        {
            task = mTask;
        }

        try
        {
            return task.performCall();
        }
        catch (Exception e)
        {
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(V v)
    {
        super.onPostExecute(v);
        if (doAfter != null)
        {
            doAfter.run(v, exception);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }

    //En caso de llamarla con get y no con execute
    public void setTask(EstudiantesCallable<V> task)
    {
        this.mTask = task;
    }

}

