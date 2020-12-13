FROM public.ecr.aws/lambda/python:3.8

RUN yum install -y mesa-libGL

# Copy function dependencies
COPY requirements.txt ${LAMBDA_TASK_ROOT}

# Install dependencies
RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# Copy function code
COPY extractframes.py ${LAMBDA_TASK_ROOT}

CMD [ "extractframes.handler_function" ]